package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/servleti/register")
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogUser newUser = new BlogUser();
        RegisterForm form = new RegisterForm();
        form.popuniIzBlogUsera(newUser);

        req.setAttribute("zapis", form);

        req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String metoda = req.getParameter("metoda");
        if (!"Save".equals(metoda)) {
            resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/main");
            return;
        }

        RegisterForm f = new RegisterForm();
        f.popuniIzHttpRequesta(req);
        f.validiraj();

        if (f.imaPogresaka()) {
            f.setPasswordHash("");
            req.setAttribute("zapis", f);
            req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
            return;
        }

        BlogUser newUser = new BlogUser();
        f.popuniUBlogUsera(newUser);

        if (DAOProvider.getDAO().getBlogUser(newUser.getNick()) != null) {
            f.setPasswordHash("");
            f.dodajGresku("nick", "Nick already in use!");
            req.setAttribute("zapis", f);
            req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
            return;
        }

        DAOProvider.getDAO().createNewUser(newUser);
        resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/main");
    }
}
