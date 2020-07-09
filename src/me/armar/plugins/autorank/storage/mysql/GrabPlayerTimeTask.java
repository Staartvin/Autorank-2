package me.armar.plugins.autorank.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * This will get the database time. <br>
 * <br>
 * We have to wait for the thread to finish before we can get the results. <br>
 * Every database lookup will have to have its own thread.
 *
 * @author Staartvin
 */
public class GrabPlayerTimeTask implements Callable<Integer> {

    private final SQLConnection mysql;
    private final String table;
    private final UUID uuid;

    public GrabPlayerTimeTask(final SQLConnection mysql, final UUID uuid, final String table) {
        this.mysql = mysql;
        this.uuid = uuid;
        this.table = table;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() {
        if (mysql == null)
            return -1;

        int time = -1;

        final String statement = "SELECT * FROM " + table + " WHERE uuid='" + uuid.toString() + "'";
        final Optional<ResultSet> rs = mysql.executeQuery(statement);

        if (!rs.isPresent())
            return time;

        try {
            if (rs.get().next()) {
                time = rs.get().getInt(2);
                rs.get().close();
            } else {
                return time;
            }

        } catch (final SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

        return time;
    }

}
