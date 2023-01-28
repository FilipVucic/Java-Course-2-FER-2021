package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/servleti/saveblogentry")
public class SaveBlogEntryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        obradi(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        obradi(req, resp);
    }

    protected void obradi(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String metoda = req.getParameter("metoda");
        if (!"Save".equals(metoda)) {
            resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/main");
            return;
        }

        BlogEntryForm f = new BlogEntryForm();
        f.popuniIzHttpRequesta(req);
        f.validiraj();

        if (f.imaPogresaka()) {
            req.setAttribute("zapis", f);
            req.getRequestDispatcher("/WEB-INF/pages/Formular.jsp").forward(req, resp);
            return;
        }

        BlogEntry blogEntry;
        if (f.getId().isEmpty()) {
            blogEntry = new BlogEntry();
        } else {
            blogEntry = DAOProvider.getDAO().getBlogEntry(Long.parseLong(f.getId()));
        }
        f.popuniUBlogEntry(blogEntry);
        blogEntry.setLastModifiedAt(new Date());

        String nick = (String) req.getSession().getAttribute("current.user.nick");
        if (blogEntry.getCreator() == null) {
            blogEntry.setCreator(DAOProvider.getDAO().getBlogUser(nick));
        }
        if (blogEntry.getCreatedAt() == null) {
            blogEntry.setCreatedAt(blogEntry.getLastModifiedAt());
        }
        //radi bez ovoga?
        blogEntry.getCreator().getBlogEntries().add(blogEntry);

        DAOProvider.getDAO().insertBlogEntry(blogEntry);
        resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/author/" + nick);
    }
}
