package oprpp2.hw02.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AckMessage extends Message {

    private final long UID;

    public AckMessage(long messageNumber, long UID) {
        super(MessageType.ACK, messageNumber);
        this.UID = UID;
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(this.getMessageType().ordinal() + 1);
            dos.writeLong(this.getMessageNumber());
            dos.writeLong(UID);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Message not serializable. Detailed message: " + e.getMessage());
        }

        return bos.toByteArray();
    }

    public long getUID() {
        return UID;
    }
}
