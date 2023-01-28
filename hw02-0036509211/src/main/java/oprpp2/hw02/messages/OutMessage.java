package oprpp2.hw02.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chat Out message. Contains UID of the user and text.
 *
 * @author Filip Vucic
 */
public class OutMessage extends Message {

    private final long UID;
    private final String messageText;

    public OutMessage(long messageNumber, long UID, String messageText) {
        super(MessageType.OUTMSG, messageNumber);
        this.UID = UID;
        this.messageText = messageText;
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(this.getMessageType().ordinal() + 1);
            dos.writeLong(this.getMessageNumber());
            dos.writeLong(UID);
            dos.writeUTF(messageText);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Message not serializable. Detailed message: " + e.getMessage());
        }

        return bos.toByteArray();
    }

    public long getUID() {
        return UID;
    }

    public String getMessageText() {
        return messageText;
    }
}
