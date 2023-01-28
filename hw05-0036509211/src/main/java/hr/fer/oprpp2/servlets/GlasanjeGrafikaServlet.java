package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;
import hr.fer.oprpp2.model.PollOption;
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
import java.util.List;

/**
 * Servlet for getting Pie-Chart as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/glasanje-grafika")
public class GlasanjeGrafikaServlet extends HttpServlet {

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
        PieDataset dataset = createDataset(pollOptions);
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
    private PieDataset createDataset(List<PollOption> pollOptions) throws IOException {
        DefaultPieDataset result = new DefaultPieDataset();

        for (PollOption options : pollOptions) {
            result.setValue(options.getOptionTitle(), options.getVotesCount());
        }

        return result;
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
