package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/servleti/main")
public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogUser user = new BlogUser();
        LoginForm form = new LoginForm();
        form.popuniIzBlogUsera(user);

        List<BlogUser> authors = DAOProvider.getDAO().getAllBlogUsers();

        req.setAttribute("zapis", form);
        req.setAttribute("authors", authors);

        req.getRequestDispatcher("/WEB-INF/pages/Main.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<BlogUser> authors = DAOProvider.getDAO().getAllBlogUsers();
        req.setAttribute("authors", authors);

        LoginForm f = new LoginForm();
        f.popuniIzHttpRequesta(req);
        f.validiraj();

        if (f.imaPogresaka()) {
            f.setPasswordHash("");
            req.setAttribute("zapis", f);
            req.getRequestDispatcher("/WEB-INF/pages/Main.jsp").forward(req, resp);
            return;
        }

        BlogUser newUser = new BlogUser();
        f.popuniUBlogUsera(newUser);

        BlogUser userInDB = DAOProvider.getDAO().getBlogUser(newUser.getNick());
        if (userInDB != null) {
            if (userInDB.getPasswordHash().equals(newUser.getPasswordHash())) {
                req.getSession().setAttribute("current.user.id", userInDB.getId());
                req.getSession().setAttribute("current.user.fn", userInDB.getFirstName());
                req.getSession().setAttribute("current.user.ln", userInDB.getLastName());
                req.getSession().setAttribute("current.user.nick", userInDB.getNick());
                req.getSession().setAttribute("current.user.email", userInDB.getEmail());
            } else {
                f.setPasswordHash("");
                f.dodajGresku("password", "Invalid password!");
                req.setAttribute("zapis", f);
                req.getRequestDispatcher("/WEB-INF/pages/Main.jsp").forward(req, resp);
                return;
            }
        } else {
            f.setPasswordHash("");
            f.dodajGresku("nick", "Not registered!");
            req.setAttribute("zapis", f);
            req.getRequestDispatcher("/WEB-INF/pages/Main.jsp").forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/servleti/main");
    }
}
