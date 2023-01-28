package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogComment;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/servleti/author/*")
public class AuthorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            req.setAttribute("poruka", "Invalid link.");
            req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
            return;
        }
        String[] importantParts = path.substring(1).split("/");
        String nick = importantParts[0];
        req.setAttribute("nick", nick);
        if (importantParts.length == 1) {
            BlogUser blogUser = DAOProvider.getDAO().getBlogUser(nick);
            if (blogUser != null) {
                List<BlogEntry> blogEntries = blogUser.getBlogEntries();
                req.setAttribute("blogEntries", blogEntries);
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntriesList.jsp").forward(req, resp);
            } else {
                req.setAttribute("poruka", "Nick does not exist.");
                req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
            }
        } else if (importantParts.length == 2) {
            if (importantParts[1].equals("new")) {
                if (req.getSession().getAttribute("current.user.nick") != null &&
                        req.getSession().getAttribute("current.user.nick").equals(nick)) {
                    newAuthorBlogEntry(req, resp);
                } else {
                    req.setAttribute("poruka", "You are not logged in!");
                    req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
                }

            } else if (importantParts[1].equals("edit")) {
                if (req.getSession().getAttribute("current.user.nick") != null &&
                        req.getSession().getAttribute("current.user.nick").equals(nick)) {
                    editAuthorBlogEntry(req, resp);
                } else {
                    req.setAttribute("poruka", "You are not logged in!");
                    req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
                }
            } else {
                Long eid = null;
                try {
                    eid = Long.parseLong(importantParts[1]);
                } catch (Exception ex) {
                    req.setAttribute("poruka", "Unsupported parameters received.");
                    req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
                    return;
                }

                BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(eid);

                if (!blogEntry.getCreator().getNick().equals(nick)) {
                    req.setAttribute("poruka", "Author of this blog entry is not " + nick);
                    req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("blogEntry", blogEntry);

                BlogComment blogComment = new BlogComment();
                BlogCommentForm f = new BlogCommentForm();
                f.popuniIzBlogCommenta(blogComment);

                req.setAttribute("zapis", f);

                req.setAttribute("entryId", blogEntry.getId());
                req.getRequestDispatcher("/WEB-INF/pages/BlogEntry.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("poruka", "Invalid link.");
            req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
        }
    }

    private void editAuthorBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long eid = null;
        try {
            eid = Long.valueOf(req.getParameter("eid"));
        } catch (Exception ex) {
            req.setAttribute("poruka", "Unsupported parameters received.");
            req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
            return;
        }

        BlogEntry blogEntry = DAOProvider.getDAO().getBlogEntry(eid);

        if (blogEntry == null) {
            req.setAttribute("poruka", "Given blog entry does not exist.");
            req.getRequestDispatcher("/WEB-INF/pages/Greska.jsp").forward(req, resp);
            return;
        }

        BlogEntryForm f = new BlogEntryForm();
        f.popuniIzBlogEntrya(blogEntry);

        req.setAttribute("zapis", f);

        req.getRequestDispatcher("/WEB-INF/pages/Formular.jsp").forward(req, resp);
    }

    private void newAuthorBlogEntry(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BlogEntry blogEntry = new BlogEntry();
        BlogEntryForm f = new BlogEntryForm();
        f.popuniIzBlogEntrya(blogEntry);

        req.setAttribute("zapis", f);

        req.getRequestDispatcher("/WEB-INF/pages/Formular.jsp").forward(req, resp);
    }
}
