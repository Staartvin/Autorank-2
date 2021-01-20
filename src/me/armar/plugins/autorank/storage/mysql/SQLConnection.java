package me.armar.plugins.autorank.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.armar.plugins.autorank.config.SettingsConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

/**
 * This class is used to create a connection between the MySQL database and
 * Autorank.
 *
 * @author Staartvin
 */
public class SQLConnection {
    private static SQLConnection instance;

    private final String database;
    private final String hostname;
    private final String password;
    private final String username;
    private final String useSSL;

    private HikariDataSource dataSource = null;

    /**
     * Create a new MySQL Connection
     *
     * @param hostname Hostname (Ex. 127.0.0.1:3306)
     * @param username Username
     * @param password Password
     * @param database Database
     * @param useSSL useSSL
     */
    private SQLConnection(final String hostname, final String username, final String password, final String database, final String useSSL) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.useSSL = useSSL;
    }

    /**
     * Get a singleton instance of the MySQL connection
     * @param configHandler The SettingsConfig to read the configuration from
     * @return The SQLConnection singleton instance
     */
    public static synchronized SQLConnection getInstance(SettingsConfig configHandler) {
        if(instance == null) {
            String hostname = configHandler.getMySQLSetting(SettingsConfig.MySQLSettings.HOSTNAME);
            String username = configHandler.getMySQLSetting(SettingsConfig.MySQLSettings.USERNAME);
            String password = configHandler.getMySQLSetting(SettingsConfig.MySQLSettings.PASSWORD);
            String database = configHandler.getMySQLSetting(SettingsConfig.MySQLSettings.DATABASE);
            String useSSL   = configHandler.getMySQLSetting(SettingsConfig.MySQLSettings.USESSL);

            instance = new SQLConnection(hostname, username, password, database, useSSL);
        }

        return instance;
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

        config.setPoolName("autorank-hikari");
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
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("useSSL", this.useSSL);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setConnectionTimeout(5000);
        config.setInitializationFailTimeout(-1);

        try {
            this.dataSource = new HikariDataSource(config);

            return isConnected();
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

        // Do not run a query when we have no connection.
        if (!isConnected()) return;

        try (Connection connection = this.getConnection().get()) {

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
     * @return ResultSet if successfully performed, empty if no connection could be made or query did not perform
     * correctly.
     */
    public Optional<ResultSet> executeQuery(final String sql) {
        ResultSet rs = null;
        PreparedStatement stmt = null;

        if (!isConnected()) {
            return Optional.empty();
        }

        try (Connection connection = this.getConnection().get()) {

            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            return Optional.of(rs);

        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.execute");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return Optional.empty();
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

    /**
     * Get the connection to be used to execute queries.
     * Note that this method will not return a connection when it's not valid. Please make sure to check before using
     * the connection.
     *
     * @return Connection object if there is a connection, or nothing if there is no connection.
     */
    public Optional<Connection> getConnection() {

        try {
            Connection connection = dataSource.getConnection();

            if (connection == null) return Optional.empty();

            if (connection.isValid(5)) {
                return Optional.of(connection);
            } else {
                connection.close();
                return Optional.empty();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Check whether a connection is made to the database.
     *
     * @return true if the connection is made and valid, false otherwise.
     */
    public boolean isConnected() {

        // Obtain connection
        Optional<Connection> connection = getConnection();

        // Check if it is valid
        boolean isValid = connection.isPresent();

        // Close connection if it is present
        connection.ifPresent(c -> {
            try {
                c.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        // Return whether we retrieved a connection.
        return isValid;
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
