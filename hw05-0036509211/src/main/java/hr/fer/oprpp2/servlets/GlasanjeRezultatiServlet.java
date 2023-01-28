package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;
import hr.fer.oprpp2.model.PollOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for getting html as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {

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
        List<PollOption> pollOptions = DAOProvider.getDao().getPollOptions(pollID);

        List<PollOption> winners = new ArrayList<>();
        winners.add(pollOptions.get(0));
        for (int i = 1; i < pollOptions.size(); i++) {
            PollOption potentialWinner = pollOptions.get(i);
            if (winners.get(0).getVotesCount() == potentialWinner.getVotesCount()) {
                winners.add(potentialWinner);
            } else {
                break;
            }
        }

        req.setAttribute(Keys.KEY_POLL_OPTIONS, pollOptions);
        req.setAttribute(Keys.KEY_WINNERS, winners);
        req.setAttribute(Keys.KEY_POLL_ID, pollID);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
    }

}
