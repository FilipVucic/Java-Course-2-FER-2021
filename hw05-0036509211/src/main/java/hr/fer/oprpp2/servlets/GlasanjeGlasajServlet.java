package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for voting input.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long pollOptionID;
        long pollID;
        try {
            pollOptionID = Long.parseLong(req.getParameter(Keys.KEY_POLL_OPTION_ID));
            pollID = Long.parseLong(req.getParameter(Keys.KEY_POLL_ID));
        } catch (NumberFormatException ex) {
            req.setAttribute("MESSAGE", "id or pollID not parsable or not passed!");
            req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
            return;
        }

        DAOProvider.getDao().vote(pollOptionID);
        resp.sendRedirect(req.getContextPath() + "/servleti/glasanje-rezultati?pollID=" + pollID);
    }
}
