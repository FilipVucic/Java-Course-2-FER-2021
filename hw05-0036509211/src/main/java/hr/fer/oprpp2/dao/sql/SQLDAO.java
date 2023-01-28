package hr.fer.oprpp2.dao.sql;

import hr.fer.oprpp2.dao.DAO;
import hr.fer.oprpp2.dao.DAOException;
import hr.fer.oprpp2.model.Poll;
import hr.fer.oprpp2.model.PollOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *
 * @author marcupic
 */
public class SQLDAO implements DAO {

    @Override
    public void vote(long ID) {
        Connection con = SQLConnectionProvider.getConnection();

        try (PreparedStatement pst = con.prepareStatement("UPDATE PollOptions SET votesCount = votesCount + 1 WHERE id = ?")) {
            pst.setLong(1, ID);
            pst.executeUpdate();
        } catch (SQLException exc) {
            throw new DAOException(exc.getMessage());
        }
    }

    @Override
    public List<PollOption> getPollOptions(long pollID) {
        Connection con = SQLConnectionProvider.getConnection();
        List<PollOption> pollOptions = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement("SELECT * FROM PollOptions WHERE pollID = ? ORDER BY votesCount DESC")) {
            pst.setLong(1, pollID);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs != null && rs.next()) {
                    PollOption options = new PollOption(rs.getLong(1), rs.getString(2),
                            rs.getString(3), rs.getLong(4), rs.getLong(5));
                    pollOptions.add(options);
                }
            }
        } catch (SQLException exc) {
            throw new DAOException(exc.getMessage());
        }

        return pollOptions;
    }

    @Override
    public List<Poll> getPolls() throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        List<Poll> polls = new ArrayList<>();

        try (PreparedStatement pst = con.prepareStatement("SELECT * FROM Polls")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs != null && rs.next()) {
                    Poll poll = new Poll(rs.getLong(1), rs.getString(2),
                            rs.getString(3));
                    polls.add(poll);
                }
            }
        } catch (SQLException exc) {
            throw new DAOException(exc.getMessage());
        }

        return polls;
    }

    @Override
    public Poll getPoll(long ID) throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        Poll poll = null;

        try (PreparedStatement pst = con.prepareStatement("SELECT id,title,message FROM Polls WHERE id = ?")) {
            pst.setLong(1, ID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs != null && rs.next()) {
                    poll = new Poll(rs.getLong(1), rs.getString(2),
                            rs.getString(3));
                }
            }
        } catch (SQLException exc) {
            throw new DAOException(exc.getMessage());
        }

        return poll;
    }

}