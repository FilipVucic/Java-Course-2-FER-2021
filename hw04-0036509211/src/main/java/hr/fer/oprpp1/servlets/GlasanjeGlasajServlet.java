package hr.fer.oprpp1.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for voting input.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException ex) {
            req.setAttribute("MESSAGE", "id not parsable or not passed!");
            req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
            return;
        }

        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
        File newFile = new File(fileName);

        if (newFile.createNewFile()) {
            log("File created");
        }

        Map<Integer, Integer> allVotes = readGlasanjeRezultati(fileName, id);

        StringBuilder results = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : allVotes.entrySet()) {
            results.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
        }
        Files.writeString(Paths.get(fileName), results.toString());
        resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");
    }

    private Map<Integer, Integer> readGlasanjeRezultati(String fileName, int id) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Map<Integer, Integer> allVotes = new HashMap<>();
        for (String line : lines) {
            String[] elements = line.split("\\t");
            int votes = Integer.parseInt(elements[1]);
            if (Integer.parseInt(elements[0]) == id) {
                votes++;
            }

            allVotes.put(Integer.parseInt(elements[0]), votes);
        }

        if (!allVotes.containsKey(id)) {
            allVotes.put(id, 1);
        }

        return allVotes;
    }
}
