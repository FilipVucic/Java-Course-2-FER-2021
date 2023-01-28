package hr.fer.oprpp2.model;

/**
 * Poll definition.
 *
 * @author Filip Vucic
 */
public class Poll {

    /**
     * Poll ID.
     */
    private final long ID;

    /**
     * Poll title.
     */
    private final String title;

    /**
     * Poll message.
     */
    private final String message;

    /**
     * Create new {@link Poll}.
     *
     * @param ID      ID
     * @param title   Title
     * @param message Message
     */
    public Poll(long ID, String title, String message) {
        this.ID = ID;
        this.title = title;
        this.message = message;
    }


    /**
     * Get ID of the poll.
     *
     * @return Poll ID
     */
    public long getID() {
        return ID;
    }

    /**
     * Get title of the poll.
     *
     * @return Poll title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get message of the poll.
     *
     * @return Poll message
     */
    public String getMessage() {
        return message;
    }
}
