package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Keys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * Servlet for setting color.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/setcolor")
public class ColorServlet extends HttpServlet {

    /**
     * Sync object.
     */
    private static final Object LOCK = new Object();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        synchronized (LOCK) {
            String bgColorParam = req.getParameter(Keys.KEY_BG_COLOR);
            String bgColor = Objects.requireNonNullElse(bgColorParam, "");
            session.setAttribute(Keys.KEY_BG_COLOR, bgColor);
        }

        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath()));
    }
}
