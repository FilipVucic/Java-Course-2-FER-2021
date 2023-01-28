package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.Keys;
import hr.fer.oprpp2.dao.DAOProvider;
import hr.fer.oprpp2.model.PollOption;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for getting xls as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/servleti/glasanje-xls")
public class GlasanjeXlsServlet extends HttpServlet {

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
        resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");
        createWorkbook(pollOptions).write(resp.getOutputStream());
    }

    private Workbook createWorkbook(List<PollOption> results) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Voting results");
        HSSFRow rowhead = sheet.createRow(0);
        rowhead.createCell(0).setCellValue("Subject");
        rowhead.createCell(1).setCellValue("Votes");

        int rowCounter = 0;
        for (PollOption result : results) {
            HSSFRow row = sheet.createRow(++rowCounter);
            row.createCell(0).setCellValue(result.getOptionTitle());
            row.createCell(1).setCellValue(result.getVotesCount());
        }

        return workbook;
    }

}
