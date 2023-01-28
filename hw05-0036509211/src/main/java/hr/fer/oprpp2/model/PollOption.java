package hr.fer.oprpp2.model;

/**
 * Entry of the poll.
 *
 * @author Filip Vucic
 */
public class PollOption {

    /**
     * Poll option ID.
     */
    private final long ID;

    /**
     * Poll option title.
     */
    private final String optionTitle;

    /**
     * Poll option link.
     */
    private final String optionLink;

    /**
     * Poll ID.
     */
    private final long pollID;

    /**
     * Votes count.
     */
    private final long votesCount;

    /**
     * Create new {@link PollOption}.
     *
     * @param ID          ID
     * @param optionTitle Option Title
     * @param optionLink  Option Link
     * @param pollID      Poll ID
     * @param votesCount  Votes Count
     */
    public PollOption(long ID, String optionTitle, String optionLink, long pollID, long votesCount) {
        this.ID = ID;
        this.optionTitle = optionTitle;
        this.optionLink = optionLink;
        this.pollID = pollID;
        this.votesCount = votesCount;
    }

    /**
     * Get option ID.
     *
     * @return Poll options ID
     */
    public long getID() {
        return ID;
    }

    /**
     * Get option title.
     *
     * @return Option title
     */
    public String getOptionTitle() {
        return optionTitle;
    }

    /**
     * Get option link.
     *
     * @return Option link
     */
    public String getOptionLink() {
        return optionLink;
    }

    /**
     * Get ID of the poll.
     *
     * @return Poll ID
     */
    public long getPollID() {
        return pollID;
    }

    /**
     * Get votes count.
     *
     * @return Votes count
     */
    public long getVotesCount() {
        return votesCount;
    }
}
