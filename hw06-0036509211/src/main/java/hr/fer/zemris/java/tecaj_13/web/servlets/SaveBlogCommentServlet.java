package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogComment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/servleti/saveblogcomment")
public class SaveBlogCommentServlet extends HttpServlet {

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
        String entryId = req.getParameter("entryId");
        String nick = req.getParameter("entryNick");

        String metoda = req.getParameter("metoda");
        if (!"Save".equals(metoda)) {
            resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/main");
            return;
        }

        BlogCommentForm f = new BlogCommentForm();
        f.popuniIzHttpRequesta(req);
        f.validiraj();


        if (f.imaPogresaka()) {
            req.setAttribute("zapis", f);
            req.setAttribute("nick", nick);
            req.setAttribute("entryId", entryId);
            req.getRequestDispatcher("/WEB-INF/pages/BlogEntry.jsp").forward(req, resp);
            return;
        }

        BlogComment blogComment = new BlogComment();

        f.popuniUBlogComment(blogComment);
        blogComment.setPostedOn(new Date());

        blogComment.setBlogEntry(DAOProvider.getDAO().getBlogEntry(Long.parseLong(entryId)));

        //radi bez ovoga?
        blogComment.getBlogEntry().getComments().add(blogComment);

        DAOProvider.getDAO().insertBlogComment(blogComment);


        resp.sendRedirect(req.getServletContext().getContextPath() + "/servleti/author/" + nick + "/" + entryId);
    }
}
