package oprpp2.hw02.server;

import oprpp2.hw02.messages.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    private final List<ClientConnection> connectedClients;
    private DatagramSocket socket;
    private long nextUIDToBeGenerated;

    public Main(int port) {
        this.connectedClients = new LinkedList<>();
        this.nextUIDToBeGenerated = (Math.abs(new Random().nextLong()));

        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Not possible to open new socket. Detailed message: " + e.getMessage());
            return;
        }
        System.out.println("Server is running.");

        this.listenForNewMessages();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("The correct input is: port.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Not a number: " + args[0]);
            return;
        }
        if (port < 1 || port > 65535) {
            System.out.println("Port must be between 1 and 65535.");
            return;
        }

        new Main(port);
    }

    private void readHello(HelloMessage helloMessage, DatagramPacket packet) {
        ClientConnection newConnection = new ClientConnection();
        newConnection.ipAddress = packet.getAddress();
        newConnection.port = packet.getPort();
        newConnection.randomHelloKey = helloMessage.getRandKey();
        if (!connectedClients.contains(newConnection)) {
            newConnection.UID = nextUIDToBeGenerated++;
            newConnection.name = helloMessage.getUserName();
            this.connectedClients.add(newConnection);
            new Thread(() -> this.sendInMessages(newConnection)).start();
        }
        final byte[] serializedMessage = new AckMessage(0L, newConnection.UID).serialize();
        final DatagramPacket packet2 = new DatagramPacket(serializedMessage, serializedMessage.length);
        packet2.setAddress(newConnection.ipAddress);
        packet2.setPort(newConnection.port);
        try {
            socket.send(packet2);
        } catch (IOException e) {
            System.out.println("Can not send the packet.");
        }
    }

    private void readAck(AckMessage ackMessage) {
        for (ClientConnection connection : connectedClients) {
            if (connection.UID == ackMessage.getUID()) {
                connection.receivedAckQueue.add(ackMessage);
            }
        }
    }

    private void readBye(ByeMessage byeMessage) {
        for (ClientConnection connection : connectedClients) {
            if (connection.UID == byeMessage.getUID()) {
                this.connectedClients.remove(connection);
                final byte[] serializedMessage = new AckMessage(byeMessage.getMessageNumber(), connection.UID).serialize();
                final DatagramPacket packet2 = new DatagramPacket(serializedMessage, serializedMessage.length);
                packet2.setAddress(connection.ipAddress);
                packet2.setPort(connection.port);
                try {
                    socket.send(packet2);
                } catch (IOException e) {
                    System.out.println("Can not send the packet.");
                }
            }
        }
    }

    private void readOut(OutMessage outMessage) {
        String senderName = null;
        for (ClientConnection connection : connectedClients) {
            if (outMessage.getUID() == connection.UID) {
                senderName = connection.name;
                final byte[] serializedMessage = new AckMessage(outMessage.getMessageNumber(), outMessage.getUID()).serialize();
                final DatagramPacket packet2 = new DatagramPacket(serializedMessage, serializedMessage.length);
                packet2.setAddress(connection.ipAddress);
                packet2.setPort(connection.port);
                try {
                    socket.send(packet2);
                } catch (IOException e) {
                    System.out.println("Can not send the packet.");
                }
                break;
            }
        }

        for (ClientConnection connection : connectedClients) {
            try {
                connection.messagesToBeSentQueue.put(new InMessage(connection.serverCounter++, senderName, outMessage.getMessageText()));
            } catch (InterruptedException e) {
                System.out.println("Queue got interrupted.");
            }
        }
    }

    private void listenForNewMessages() {
        byte[] buf = new byte[4000];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (true) {
            if (socket.isClosed()) {
                return;
            }
            try {
                socket.receive(packet);
            } catch (IOException e) {
                continue;
            }

            Message deserializedMessage;
            try {
                deserializedMessage = MessageUtil.deserializeMessage(packet.getData(),
                        packet.getOffset(), packet.getLength());
            } catch (IOException e) {
                System.out.println("Message not deserializable. Detailed message: " + e.getMessage());
                continue;
            }

            switch (deserializedMessage.getMessageType()) {
                case HELLO -> readHello((HelloMessage) deserializedMessage, packet);

                case ACK -> readAck((AckMessage) deserializedMessage);

                case BYE -> readBye((ByeMessage) deserializedMessage);

                case OUTMSG -> readOut((OutMessage) deserializedMessage);

            }
        }
    }

    /**
     * @param client
     */
    private void sendInMessages(ClientConnection client) {
        while (true) {
            Message message;
            try {
                message = client.messagesToBeSentQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Queue got interrupted.");
                continue;
            }

            byte[] serializedOutMessage = message.serialize();

            DatagramPacket packet = new DatagramPacket(serializedOutMessage, serializedOutMessage.length);
            packet.setAddress(client.ipAddress);
            packet.setPort(client.port);

            int retransmissionCounter = 0;
            while (retransmissionCounter < 10) {
                retransmissionCounter++;

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    System.out.println("Can not send the packet.");
                    break;
                }

                Message ackMessage;
                try {
                    ackMessage = client.receivedAckQueue.poll(5L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.out.println("Queue got interrupted.");
                    continue;
                }

                if (ackMessage == null) {
                    continue;
                }

                if (ackMessage.getMessageNumber() == message.getMessageNumber()) {
                    break;
                }
            }
            if (retransmissionCounter == 10) {
                connectedClients.remove(client);
                return;
            }
        }

    }

    /**
     * One client chat connection.
     */
    private static class ClientConnection {
        private final BlockingQueue<Message> receivedAckQueue;
        private final BlockingQueue<Message> messagesToBeSentQueue;
        private String name;
        private long UID;
        private long randomHelloKey;
        private InetAddress ipAddress;
        private int port;
        private long serverCounter = 1;

        private ClientConnection() {
            receivedAckQueue = new LinkedBlockingQueue<>();
            messagesToBeSentQueue = new LinkedBlockingQueue<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientConnection that = (ClientConnection) o;
            return randomHelloKey == that.randomHelloKey &&
                    port == that.port &&
                    ipAddress.equals(that.ipAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(randomHelloKey, ipAddress, port);
        }
    }
}
