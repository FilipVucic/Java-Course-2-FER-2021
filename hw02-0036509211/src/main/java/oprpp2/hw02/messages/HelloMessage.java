package oprpp2.hw02.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chat Hello message. Contains user name and randKey.
 *
 * @author Filip Vucic
 */
public class HelloMessage extends Message {

    private final String userName;
    private final long randKey;

    public HelloMessage(long messageNumber, String userName, long randKey) {
        super(MessageType.HELLO, messageNumber);
        this.userName = userName;
        this.randKey = randKey;
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(this.getMessageType().ordinal() + 1);
            dos.writeLong(this.getMessageNumber());
            dos.writeUTF(userName);
            dos.writeLong(randKey);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Message not serializable. Detailed message: " + e.getMessage());
        }

        return bos.toByteArray();
    }

    public String getUserName() {
        return userName;
    }

    public long getRandKey() {
        return randKey;
    }
}
