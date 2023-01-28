package hr.fer.oprpp2.dao;

import hr.fer.oprpp2.model.Poll;
import hr.fer.oprpp2.model.PollOption;

import java.util.List;

/**
 * Sučelje prema podsustavu za perzistenciju podataka.
 *
 * @author marcupic
 */
public interface DAO {

    /**
     * Get all Poll options for poll ID.
     *
     * @param pollID Poll ID
     * @return List of Poll options
     * @throws DAOException if something is wrong with database
     */
    List<PollOption> getPollOptions(long pollID) throws DAOException;

    /**
     * Get all Polls.
     *
     * @return List of Polls
     * @throws DAOException if something is wrong with database
     */
    List<Poll> getPolls() throws DAOException;

    /**
     * Vote for an {@link PollOption}.
     *
     * @param ID ID of the {@link PollOption}
     */
    void vote(long ID);

    /**
     * Get {@link Poll} for ID.
     *
     * @param ID Poll ID
     * @return {@link Poll}
     * @throws DAOException if something is wrong with database
     */
    Poll getPoll(long ID);
}