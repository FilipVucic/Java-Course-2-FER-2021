package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Keys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for getting sin and cos values of x.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/trigonometric")
public class TrigonometricServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int a = 0;
        int b = 360;
        try {
            a = Integer.parseInt(req.getParameter("a"));
            b = Integer.parseInt(req.getParameter("b"));
        } catch (NumberFormatException ignored) {
        }

        if (a > b) {
            int tempA = a;
            a = b;
            b = tempA;
        }

        if (b > a + 720) {
            b = a + 720;
        }

        Map<Integer, Double[]> angleWithSinCos = new HashMap<>();

        for (int angle = a; angle <= b; angle++) {
            Double[] sinAndCos = new Double[2];
            sinAndCos[0] = Math.sin(angle * Math.PI / 180);
            sinAndCos[1] = Math.cos(angle * Math.PI / 180);
            angleWithSinCos.put(angle, sinAndCos);
        }

        req.setAttribute(Keys.KEY_ANGLE_TRIG, angleWithSinCos);

        req.getRequestDispatcher("WEB-INF/pages/trigonometric.jsp").forward(req, resp);
    }
}
