package oprpp2.hw02.messages;

/**
 * Chat message.
 *
 * @author Filip Vucic
 */
public abstract class Message {
    private final MessageType messageType;
    private final long messageNumber;

    public Message(MessageType messageType, long messageNumber) {
        this.messageType = messageType;
        this.messageNumber = messageNumber;
    }

    public long getMessageNumber() {
        return messageNumber;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Convert {@link Message} to array of bytes.
     *
     * @return Array of bytes
     */
    public abstract byte[] serialize();
}
