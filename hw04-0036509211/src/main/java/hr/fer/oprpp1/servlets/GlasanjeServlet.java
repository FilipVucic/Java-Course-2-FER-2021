package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Keys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Servlet for showing all bands info.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/glasanje")
public class GlasanjeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Map<Integer, String[]> bands = new TreeMap<>();
        for (String line : lines) {
            String[] elements = line.split("\\t");
            bands.put(Integer.parseInt(elements[0]), new String[]{elements[1], elements[2]});
        }

        req.setAttribute(Keys.KEY_BANDS, bands);
        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
