package me.armar.plugins.autorank.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private HikariDataSource dataSource = null;

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
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Open a new MySQL connection
     *
     * @return true if connection was successfully set up.
     */
    public boolean connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + this.hostname + "/" + this.database);
//        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        try {
            this.dataSource = new HikariDataSource(config);
            return this.dataSource != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Execute a query. Query cannot be null. This query doesn't return
     * anything. (Good for updating tables)
     *
     * @param sql Query to execute
     */
    public void execute(final String sql) {

        PreparedStatement stmt = null;

        try (Connection connection = this.getConnection()) {

            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();

        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.execute");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {

            this.close(null, stmt, null);
        }

    }

    /**
     * Convenience method for execute multiple queries at once. Does not return anything, just sends the queries to
     * the database.
     *
     * @param queries Queries to sent.
     */
    public void executeQueries(Collection<String> queries) {
        for (String query : queries) {
            this.execute(query);
        }
    }

    /**
     * Execute a query and returns a ResultSet. Query cannot be null.
     *
     * @param sql Query to execute
     * @return ResultSet if successfully performed, null if an error occured.
     */
    public ResultSet executeQuery(final String sql) {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try (Connection connection = this.getConnection()) {

            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.execute");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return rs;
    }

    /**
     * Returns state of MySQL connection
     *
     * @return true if closed, false if open.
     */
    public boolean isClosed() {
        if (dataSource == null) return true;
        return dataSource.isClosed();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }

}
