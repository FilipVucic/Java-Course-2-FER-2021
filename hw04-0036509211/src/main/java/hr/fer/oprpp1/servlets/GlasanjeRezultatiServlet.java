package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Keys;
import hr.fer.oprpp1.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Servlet for getting html as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
        File newFile = new File(fileName);

        if (newFile.createNewFile()) {
            log("File created");
        }
        Map<Integer, Integer> glasanjeRezultati = readGlasanjeRezultati(fileName);

        String fileName2 = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");

        List<String> lines = Files.readAllLines(Paths.get(fileName2));

        List<Result> results = new ArrayList<>();
        List<String[]> winners = new ArrayList<>();
        int highestVotes = 0;
        for (String line : lines) {
            String[] elements = line.split("\\t");
            Integer votes = glasanjeRezultati.get(Integer.parseInt(elements[0]));
            if (votes != null) {
                results.add(new Result(elements[1], votes));
                if (votes > highestVotes) {
                    highestVotes = votes;
                    winners.clear();
                    winners.add(new String[]{elements[1], elements[2]});
                } else if (votes == highestVotes) {
                    winners.add(new String[]{elements[1], elements[2]});
                }
            } else {
                results.add(new Result(elements[1], 0));
            }
        }
        results.sort(Comparator.comparingInt(Result::getVotes).reversed());
        req.setAttribute(Keys.KEY_BAND_VOTING, results);
        req.setAttribute(Keys.KEY_BAND_WINNERS, winners);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);

    }


    private Map<Integer, Integer> readGlasanjeRezultati(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Map<Integer, Integer> allVotes = new HashMap<>();
        for (String line : lines) {
            String[] elements = line.split("\\t");

            allVotes.put(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]));
        }

        return allVotes;
    }
}
