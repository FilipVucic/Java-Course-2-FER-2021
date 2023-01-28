package oprpp2.hw02.messages;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageUtil {

    /**
     * Deserializes message from given byte array to {@link Message} object.
     *
     * @param buf    Byte array - buffer
     * @param offset Offset
     * @param length Length
     * @return {@link Message} object
     * @throws RuntimeException when invalid message type
     * @throws IOException      when can't deserialize the message
     */
    public static Message deserializeMessage(byte[] buf, int offset, int length) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buf, offset, length));

        int messageTypeValues = MessageType.values().length;
        int messageTypeKey = dataInputStream.readByte();
        if (messageTypeKey > messageTypeValues) {
            throw new RuntimeException("Invalid message type key: " + messageTypeKey);
        }

        MessageType messageType = MessageType.values()[messageTypeKey - 1];
        switch (messageType) {
            case HELLO -> {
                return new HelloMessage(dataInputStream.readLong(), dataInputStream.readUTF(), dataInputStream.readLong());
            }
            case ACK -> {
                return new AckMessage(dataInputStream.readLong(), dataInputStream.readLong());
            }
            case BYE -> {
                return new ByeMessage(dataInputStream.readLong(), dataInputStream.readLong());
            }
            case OUTMSG -> {
                return new OutMessage(dataInputStream.readLong(), dataInputStream.readLong(), dataInputStream.readUTF());
            }
            case INMSG -> {
                return new InMessage(dataInputStream.readLong(), dataInputStream.readUTF(), dataInputStream.readUTF());
            }
            default -> throw new RuntimeException("Invalid message type: " + messageType);
        }
    }
}
