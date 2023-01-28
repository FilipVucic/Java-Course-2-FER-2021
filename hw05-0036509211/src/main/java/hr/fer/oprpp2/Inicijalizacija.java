package hr.fer.oprpp2;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Properties;

@WebListener
public class Inicijalizacija implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(sce.getServletContext().getRealPath("/WEB-INF/dbsettings.properties"))) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("File not found.");
        }

        String host = properties.getProperty("host");
        String port = properties.getProperty("port");
        String dbName = properties.getProperty("name");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        if (host == null || port == null || dbName == null || user == null || password == null) {
            throw new RuntimeException("Properties not defined properly!");
        }

        String connectionURL = "jdbc:derby://" + host + ":" + port + "/" + dbName;

        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("org.apache.derby.client.ClientAutoloadedDriver");
        } catch (PropertyVetoException e1) {
            throw new RuntimeException("Pogreška prilikom inicijalizacije poola.", e1);
        }
        cpds.setJdbcUrl(connectionURL);
        cpds.setUser(user);
        cpds.setPassword(password);

        try (Connection con = cpds.getConnection()) {
            createPolls(con);
            createPollOptions(con);
            if (pollsIsEmpty(con)) {
                fillPollsAndPollOptions(con, sce);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(1);
        }

        sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComboPooledDataSource cpds = (ComboPooledDataSource) sce.getServletContext().getAttribute("hr.fer.zemris.dbpool");
        if (cpds != null) {
            try {
                DataSources.destroy(cpds);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void createPolls(Connection con) throws SQLException {
        //con.getMetaData().getTables()
        try (PreparedStatement pst = con.prepareStatement("CREATE TABLE Polls\n" +
                " (id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,\n" +
                " title VARCHAR(150) NOT NULL,\n" +
                " message CLOB(2048) NOT NULL\n" +
                ")\n")) {
            pst.executeUpdate();
        } catch (SQLException exc) {
            //if table already exists
            if (exc.getSQLState().equals("X0Y32")) {
                return;
            }
            throw exc;
        }
    }

    private void createPollOptions(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("CREATE TABLE PollOptions\n" +
                " (id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,\n" +
                " optionTitle VARCHAR(100) NOT NULL,\n" +
                " optionLink VARCHAR(150) NOT NULL,\n" +
                " pollID BIGINT,\n" +
                " votesCount BIGINT,\n" +
                " FOREIGN KEY (pollID) REFERENCES Polls(id)\n" +
                ")")) {
            pst.executeUpdate();
        } catch (SQLException exc) {
            //if table already exists
            if (exc.getSQLState().equals("X0Y32")) {
                return;
            }
            throw exc;
        }
    }

    private boolean pollsIsEmpty(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("SELECT * FROM Polls")) {
            try (ResultSet rs = pst.executeQuery()) {
                return !(rs != null && rs.next());
            }
        }
    }

    private void fillPollsAndPollOptions(Connection con, ServletContextEvent sce) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("INSERT INTO Polls (title, message) values (?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, "Glasanje za omiljeni bend:");
            pst.setString(2, "Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!");
            pst.executeUpdate();
            String fileName = sce.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
            parseFile(con, pst, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try (PreparedStatement pst = con.prepareStatement("INSERT INTO Polls (title, message) values (?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, "Glasanje za omiljenu menzu:");
            pst.setString(2, "Od sljedećih menzi, koja Vam je menza najdraža? Kliknite na link kako biste glasali!");
            pst.executeUpdate();
            String fileName = sce.getServletContext().getRealPath("/WEB-INF/glasanjemenza-definicija.txt");
            parseFile(con, pst, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void parseFile(Connection con, PreparedStatement pst, String fileName) throws IOException, SQLException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        try (ResultSet rs = pst.getGeneratedKeys()) {
            if (rs != null && rs.next()) {
                long pollID = rs.getLong(1);
                for (String line : lines) {
                    String[] elements = line.split("\\t");
                    try (PreparedStatement pst2 = con.prepareStatement("INSERT INTO PollOptions (optionTitle, optionLink, pollID, votesCount) values (?,?,?,0)")) {
                        pst2.setString(1, elements[1]);
                        pst2.setString(2, elements[2]);
                        pst2.setLong(3, pollID);
                        pst2.executeUpdate();
                        pst2.clearParameters();
                    }
                }
            }
        }
    }

}
