package hr.fer.oprpp1.servlets;

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

/**
 * Servlet for getting powers of i. Output is written in xml file.
 *
 * @author Filip Vucic
 */
@WebServlet("/powers")
public class PowersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int a;
        int b;
        int n;
        try {
            a = Integer.parseInt(req.getParameter("a"));
            b = Integer.parseInt(req.getParameter("b"));
            n = Integer.parseInt(req.getParameter("n"));
        } catch (NumberFormatException ex) {
            req.setAttribute("MESSAGE", "Power arguments not parsable!");
            req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
            return;
        }

        if (a < -100 || a > 100 || b < -100 || b > 100 || n < 1 || n > 5) {
            req.setAttribute("MESSAGE", "Invalid range of power arguments!");
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
            return;
        }

        if (a > b) {
            int tempA = a;
            a = b;
            b = tempA;
        }

        resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");
        createWorkbook(a, b, n).write(resp.getOutputStream());
    }

    private Workbook createWorkbook(int a, int b, int n) {
        HSSFWorkbook workbook = new HSSFWorkbook();


        for (int i = 1; i <= n; i++) {
            HSSFSheet sheet = workbook.createSheet("Power of " + i);
            HSSFRow rowhead = sheet.createRow(0);
            rowhead.createCell(0).setCellValue("n");
            rowhead.createCell(1).setCellValue("pow(n," + i + ")");
            for (int j = 0; j <= b - a; j++) {
                HSSFRow row = sheet.createRow(j + 1);
                row.createCell(0).setCellValue(a + j);
                row.createCell(1).setCellValue(Math.pow(a + j, i));
            }
        }

        return workbook;
    }
}
