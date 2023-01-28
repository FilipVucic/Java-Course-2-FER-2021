package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Result;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Servlet for getting Pie-Chart as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/glasanje-grafika")
public class GlasanjeGrafikaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
        String fileName2 = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
        PieDataset dataset = createDataset(fileName, fileName2);
        JFreeChart chart = createChart(dataset, "Glasanje grafika");


        resp.setContentType("image/png");
        OutputStream outputStream = resp.getOutputStream();
        int width = 500;
        int height = 350;

        ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
    }

    /**
     * Creates a sample dataset
     */
    private PieDataset createDataset(String fileName, String fileName2) throws IOException {
        DefaultPieDataset result = new DefaultPieDataset();

        List<Result> results = getResults(fileName, fileName2);
        for (Result result1 : results) {
            result.setValue(result1.getName(), result1.getVotes());
        }
        return result;
    }

    private List<Result> getResults(String fileName, String fileName2) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Map<Integer, Integer> allVotes = new HashMap<>();
        for (String line : lines) {
            String[] elements = line.split("\\t");

            allVotes.put(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]));
        }

        List<String> lines2 = Files.readAllLines(Paths.get(fileName2));
        List<Result> results = new ArrayList<>();
        for (String line : lines2) {
            String[] elements = line.split("\\t");
            Integer votes = allVotes.get(Integer.parseInt(elements[0]));
            if (votes != null) {
                results.add(new Result(elements[1], votes));
            } else {
                results.add(new Result(elements[1], 0));
            }
        }
        results.sort(Comparator.comparingInt(Result::getVotes).reversed());

        return results;
    }

    /**
     * Creates a chart
     */
    private JFreeChart createChart(PieDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createPieChart3D(
                title,                  // chart title
                dataset,                // data
                true,                   // include legend
                true,
                false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;
    }
}
