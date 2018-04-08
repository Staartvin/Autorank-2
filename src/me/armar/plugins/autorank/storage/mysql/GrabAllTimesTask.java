package me.armar.plugins.autorank.storage.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * This will get all database times. <br>
 * <br>
 * We have to wait for the thread to finish before we can get the results. <br>
 * Every database lookup will have to have its own thread.
 *
 * @author Staartvin
 */
public class GrabAllTimesTask implements Callable<HashMap<UUID, Integer>> {

    private final SQLDataStorage mysql;
    private final String table;
    private HashMap<UUID, Integer> times = new HashMap<>();

    public GrabAllTimesTask(final SQLDataStorage mysql, final String table) {
        this.mysql = mysql;
        this.table = table;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public HashMap<UUID, Integer> call() {
        if (mysql == null)
            return times;

        final String statement = "SELECT * FROM " + table;
        final ResultSet rs = mysql.executeQuery(statement);

        if (rs == null)
            return times;

        try {
            while (rs.next()) {
                times.put(UUID.fromString(rs.getString(1)), rs.getInt(2));
            }
        } catch (final SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

        return times;
    }

}
