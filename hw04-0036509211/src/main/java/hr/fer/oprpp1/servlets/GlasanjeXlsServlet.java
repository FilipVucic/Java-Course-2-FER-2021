package hr.fer.oprpp1.servlets;

import hr.fer.oprpp1.Result;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Servlet for getting xls as output result of voting.
 *
 * @author Filip Vucic
 */
@WebServlet(urlPatterns = "/glasanje-xls")
public class GlasanjeXlsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
        String fileName2 = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
        resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");
        createWorkbook(getResults(fileName, fileName2)).write(resp.getOutputStream());
    }

    private Workbook createWorkbook(List<Result> results) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Voting results");
        HSSFRow rowhead = sheet.createRow(0);
        rowhead.createCell(0).setCellValue("Band");
        rowhead.createCell(1).setCellValue("Votes");

        int rowCounter = 0;
        for (Result result : results) {
            HSSFRow row = sheet.createRow(++rowCounter);
            row.createCell(0).setCellValue(result.getName());
            row.createCell(1).setCellValue(result.getVotes());
        }

        return workbook;
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
}
