package hr.fer.zemris.java.tecaj_13.dao;

import hr.fer.zemris.java.tecaj_13.model.BlogComment;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import java.util.List;

public interface DAO {

    /**
     * Dohvaća entry sa zadanim <code>id</code>-em. Ako takav entry ne postoji,
     * vraća <code>null</code>.
     *
     * @param id ključ zapisa
     * @return entry ili <code>null</code> ako entry ne postoji
     * @throws DAOException ako dođe do pogreške pri dohvatu podataka
     */
    BlogEntry getBlogEntry(Long id) throws DAOException;

    BlogUser getBlogUser(String nick) throws DAOException;

    void createNewUser(BlogUser user) throws DAOException;

    List<BlogUser> getAllBlogUsers() throws DAOException;

    void insertBlogEntry(BlogEntry blogEntry) throws DAOException;

    void insertBlogComment(BlogComment blogComment) throws DAOException;

}