package oprpp2.hw02.client;

import oprpp2.hw02.messages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame {
    private final InetAddress ipAddress;
    private final int port;
    private final String name;
    private final BlockingQueue<Message> receivedAckQueue;
    private final BlockingQueue<Message> messagesToBeSentQueue;
    private DatagramSocket socket;
    private long UID;
    private long clientCounter;
    private long serverCounter;
    private JTextField textField;
    private JTextArea textArea;

    public Main(InetAddress ipAddress, int port, String name) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.name = name;
        this.receivedAckQueue = new LinkedBlockingQueue<>();
        this.messagesToBeSentQueue = new LinkedBlockingQueue<>();
        this.establishConnection();

        this.setTitle("Chat client: " + this.name);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                byte[] serializedByeMessage = new ByeMessage(clientCounter++, UID).serialize();
                DatagramPacket packet = new DatagramPacket(serializedByeMessage, serializedByeMessage.length);
                packet.setAddress(ipAddress);
                packet.setPort(port);
                try {
                    socket.send(packet);
                } catch (IOException ex) {
                    System.out.println("Can not send the closing packet.");
                }
                socket.close();
                System.exit(0);
            }
        });
        this.initGUI();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("The correct input is: IP port name.");
            return;
        }

        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Not a number: " + args[1]);
            return;
        }
        if (port < 1 || port > 65535) {
            System.out.println("Port must be between 1 and 65535.");
            return;
        }

        String name = args[2];

        SwingUtilities.invokeLater(() -> new Main(ipAddress, port, name));
    }

    private void initGUI() {
        this.getContentPane().setLayout(new BorderLayout());
        this.textField = new JTextField();
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.getContentPane().add(textField, BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);

        new Thread(this::listenForNewMessages).start();
        new Thread(this::sendOutMessages).start();
        textField.addActionListener(e -> {
            String msg = this.textField.getText();
            this.textField.setText("");
            this.textField.setEnabled(false);
            if (!msg.isEmpty()) {
                messagesToBeSentQueue.add(new OutMessage(clientCounter++, UID, msg));
            }
        });
        this.setSize(600, 400);
        this.setVisible(true);
    }

    private void sendOutMessages() {
        while (true) {
            Message outMessage;
            try {
                if (socket.isClosed()) {
                    return;
                }
                outMessage = messagesToBeSentQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Queue got interrupted.");
                continue;
            }

            byte[] serializedOutMessage = outMessage.serialize();

            DatagramPacket packet = new DatagramPacket(serializedOutMessage, serializedOutMessage.length);
            packet.setAddress(ipAddress);
            packet.setPort(port);

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
                    ackMessage = receivedAckQueue.poll(5L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.out.println("Queue got interrupted.");
                    continue;
                }

                if (ackMessage == null) {
                    continue;
                }

                if (ackMessage.getMessageNumber() == outMessage.getMessageNumber()) {
                    break;
                }
            }
            if (retransmissionCounter == 10) {
                return;
            }
            this.textField.setEnabled(true);
            this.textField.requestFocusInWindow();
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

            if (deserializedMessage.getMessageType().equals(MessageType.ACK)) {
                try {
                    this.receivedAckQueue.put(deserializedMessage);
                } catch (InterruptedException e) {
                    System.out.println("Queue got interrupted.");
                }
            } else if (deserializedMessage.getMessageType().equals(MessageType.INMSG)) {
                InMessage inMessage = (InMessage) deserializedMessage;
                if (deserializedMessage.getMessageNumber() != this.serverCounter + 1L) {
                    System.out.println("Got message number: " + deserializedMessage.getMessageNumber());
                    continue;
                }

                serverCounter++;
                this.textArea.append("[" + packet.getSocketAddress() + "] " + "Poruka od korisnika: " +
                        inMessage.getName() + "\n" + inMessage.getMessageText() + "\n\n");

                byte[] serializedAckMessage = new AckMessage(inMessage.getMessageNumber(), this.UID).serialize();
                DatagramPacket packet2 = new DatagramPacket(serializedAckMessage, serializedAckMessage.length);
                packet2.setAddress(ipAddress);
                packet2.setPort(port);
                try {
                    socket.send(packet2);
                } catch (IOException e) {
                    System.out.println("Can not send the packet.");
                }
            }
        }
    }

    private void establishConnection() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Not possible to open new socket. Detailed message: " + e.getMessage());
            System.exit(1);
        }

        byte[] serializedHelloMessage = new HelloMessage(clientCounter++, name, new Random().nextLong()).serialize();
        DatagramPacket packet = new DatagramPacket(serializedHelloMessage, serializedHelloMessage.length);
        packet.setAddress(ipAddress);
        packet.setPort(port);

        byte[] buf = new byte[4000];
        DatagramPacket packet2 = new DatagramPacket(buf, buf.length);

        int retransmissionCounter = 0;
        boolean receivedAck = false;

        while (retransmissionCounter < 10) {
            retransmissionCounter++;

            try {
                socket.send(packet);
            } catch (IOException e) {
                System.out.println("Can not send the packet.");
                break;
            }

            try {
                socket.setSoTimeout(5000);
            } catch (SocketException ignored) {
            }

            try {
                socket.receive(packet2);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                break;
            }

            Message deserializedMessage;
            try {
                deserializedMessage = MessageUtil.deserializeMessage(packet2.getData(),
                        packet2.getOffset(), packet2.getLength());
            } catch (IOException e) {
                System.out.println("Message not deserializable. Detailed message: " + e.getMessage());
                continue;
            }

            if (deserializedMessage.getMessageType().equals(MessageType.ACK)) {
                receivedAck = true;
                this.UID = ((AckMessage) deserializedMessage).getUID();
                break;
            }
        }

        if (!receivedAck) {
            System.out.println("Connection not established!");
            System.exit(1);
        }

        try {
            socket.setSoTimeout(0);
        } catch (SocketException ignored) {
        }
        System.out.println("Connection established. UID: " + UID);
    }
}
