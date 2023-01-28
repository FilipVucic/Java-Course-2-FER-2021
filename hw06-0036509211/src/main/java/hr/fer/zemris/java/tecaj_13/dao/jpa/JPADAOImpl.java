package hr.fer.zemris.java.tecaj_13.dao.jpa;

import hr.fer.zemris.java.tecaj_13.dao.DAO;
import hr.fer.zemris.java.tecaj_13.dao.DAOException;
import hr.fer.zemris.java.tecaj_13.model.BlogComment;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import java.util.List;

public class JPADAOImpl implements DAO {

    @Override
    public BlogEntry getBlogEntry(Long id) throws DAOException {
        return JPAEMProvider.getEntityManager().find(BlogEntry.class, id);
    }

    @Override
    public BlogUser getBlogUser(String nick) throws DAOException {
        List<BlogUser> users = JPAEMProvider.getEntityManager().createNamedQuery("BlogUser.upit1", BlogUser.class)
                .setParameter("nick", nick)
                .getResultList();

        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    @Override
    public void createNewUser(BlogUser user) throws DAOException {
        JPAEMProvider.getEntityManager().persist(user);
    }

    @Override
    public List<BlogUser> getAllBlogUsers() throws DAOException {
        return JPAEMProvider.getEntityManager().createNamedQuery("BlogUser.upit2", BlogUser.class).getResultList();
    }

    @Override
    public void insertBlogEntry(BlogEntry blogEntry) throws DAOException {
        if (blogEntry.getId() == null) {
            JPAEMProvider.getEntityManager().persist(blogEntry);
        } else {
            JPAEMProvider.getEntityManager().merge(blogEntry);
        }
    }

    @Override
    public void insertBlogComment(BlogComment blogComment) throws DAOException {
        JPAEMProvider.getEntityManager().persist(blogComment);
    }

}