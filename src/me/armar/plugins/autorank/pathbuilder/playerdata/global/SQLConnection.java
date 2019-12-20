package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import java.sql.*;
import java.util.Collection;

/**
 * This class is used to create a connection between the MySQL database and
 * Autorank.
 *
 * @author Staartvin
 */
public class SQLConnection {

    private final String database;
    private final String hostname;
    private final String password;
    private final String username;
    private Connection conn = null;

    /**
     * Create a new MySQL Connection
     *
     * @param hostname Hostname (Ex. 127.0.0.1:3306)
     * @param username Username
     * @param password Password
     * @param database Database
     */
    public SQLConnection(final String hostname, final String username, final String password, final String database) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    /**
     * Tries to close the MySQL connection. If already closed, nothing will
     * happen.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open a new MySQL connection
     *
     * @return true if connection was successfully set up.
     */
    public boolean connect() {
        try {
            final String url = "jdbc:mysql://" + hostname + "/" + database + "?useSSL=false";

            conn = DriverManager.getConnection(url, username, password);

        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.connect");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

            return false;
        } catch (final Exception e) {

            e.printStackTrace();
        }
        return conn != null;
    }

    /**
     * Execute a query. Query cannot be null. This query doesn't return
     * anything. (Good for updating tables)
     *
     * @param sql Query to execute
     */
    public void performUpdate(final String sql) {
        PreparedStatement stmt = null;

        if (conn != null) {
            try {

                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();

            } catch (final SQLException ex) {
                System.out.println("SQLDataStorage.execute");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (final SQLException sqlEx) {
                    }

                    stmt = null;
                }
            }
        }
    }

    /**
     * Convenience method for execute multiple queries at once. Does not return anything, just sends the queries to
     * the database.
     *
     * @param queries Queries to sent.
     */
    public void performUpdates(Collection<String> queries) {
        for (String query : queries) {
            this.performUpdate(query);
        }
    }

    /**
     * Execute a query and returns a ResultSet. Query cannot be null.
     *
     * @param sql Query to execute
     * @return ResultSet if successfully performed, null if an error occured.
     */
    public ResultSet performQuery(final String sql) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (conn != null) {
            try {

                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();

            } catch (final SQLException ex) {
                System.out.println("SQLDataStorage.executeQuery");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());

            }
        }
        return rs;
    }

    /**
     * Returns state of MySQL connection
     *
     * @return true if closed, false if open.
     */
    public boolean isClosed() {

        if (conn == null)
            return true;

        try {
            return conn.isClosed();
        } catch (final SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

}
