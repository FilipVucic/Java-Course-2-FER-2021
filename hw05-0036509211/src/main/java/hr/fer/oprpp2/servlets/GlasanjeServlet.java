package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;
import hr.fer.oprpp2.model.Poll;
import hr.fer.oprpp2.model.PollOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for showing all bands info.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/glasanje")
public class GlasanjeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long pollID;
        try {
            pollID = Long.parseLong(req.getParameter(Keys.KEY_POLL_ID));
        } catch (NumberFormatException ex) {
            req.setAttribute("MESSAGE", "id not parsable or not passed!");
            req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
            return;
        }

        Poll poll = DAOProvider.getDao().getPoll(pollID);
        List<PollOption> pollOptions = DAOProvider.getDao().getPollOptions(pollID);

        req.setAttribute(Keys.KEY_POLL_TITLE, poll.getTitle());
        req.setAttribute(Keys.KEY_POLL_MESSAGE, poll.getMessage());
        req.setAttribute(Keys.KEY_POLL_OPTIONS, pollOptions);
        req.setAttribute(Keys.KEY_POLL_ID, pollID);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
