package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;
import hr.fer.oprpp2.model.Poll;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Index servlet.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/index.html")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Poll> polls = DAOProvider.getDao().getPolls();
        req.setAttribute(Keys.KEY_POLLS, polls);
        req.getRequestDispatcher("/WEB-INF/pages/index.jsp").forward(req, resp);
    }

}
