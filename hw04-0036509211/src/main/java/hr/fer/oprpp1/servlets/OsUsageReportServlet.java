package hr.fer.oprpp1.servlets;

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

/**
 * Servlet for getting OS usage report.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/reportImage")
public class OsUsageReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PieDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset, "OS usage");

        resp.setContentType("image/png");
        OutputStream outputStream = resp.getOutputStream();
        int width = 500;
        int height = 350;

        ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
    }

    /**
     * Creates a sample dataset
     */
    private PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
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
