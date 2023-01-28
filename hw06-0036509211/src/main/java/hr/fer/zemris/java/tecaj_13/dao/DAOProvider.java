package hr.fer.zemris.java.tecaj_13.dao;

import hr.fer.zemris.java.tecaj_13.dao.jpa.JPADAOImpl;

public class DAOProvider {

    private static final DAO dao = new JPADAOImpl();

    public static DAO getDAO() {
        return dao;
    }

}