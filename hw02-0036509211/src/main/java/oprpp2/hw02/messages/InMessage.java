package oprpp2.hw02.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chat In message. Contains name of the user and text.
 *
 * @author Filip Vucic
 */
public class InMessage extends Message {

    private final String name;
    private final String messageText;

    public InMessage(long messageNumber, String name, String messageText) {
        super(MessageType.INMSG, messageNumber);
        this.name = name;
        this.messageText = messageText;
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeByte(this.getMessageType().ordinal() + 1);
            dos.writeLong(this.getMessageNumber());
            dos.writeUTF(name);
            dos.writeUTF(messageText);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException("Message not serializable. Detailed message: " + e.getMessage());
        }

        return bos.toByteArray();
    }

    public String getName() {
        return name;
    }

    public String getMessageText() {
        return messageText;
    }
}
