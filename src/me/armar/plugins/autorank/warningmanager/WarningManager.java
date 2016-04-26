package me.armar.plugins.autorank.warningmanager;

import java.util.HashMap;
import java.util.Map.Entry;

import me.armar.plugins.autorank.Autorank;

/**
 * This class handles all warning message that are displayed when something is
 * wrong with Autorank.
 * Instead of disabling Autorank, it should display warnings when an admin
 * joins.
 * 
 * <p>
 * Warnings will be stored in a list. Every warning will have its own priority.
 * Whenever an player joins that has the permission
 * '<i>autorank.warning.notice</i>', the warning manager will grab the warning
 * with the highest priority and show it to the player. This way the admin knows
 * something is up and he has to fix it.
 * 
 * <p>
 * The highest priority warning will be saved as a message so it doesn't have to
 * be recalculated every time someone joins. It will be determined when the
 * first player that has the correct permission joins.
 * 
 * <p>
 * The priority level goes from <b>1 to 10</b>, with 1 being the lowest priority
 * and thus no need for attention, and 10 the highest, which represents critical
 * failures.
 * 
 * <p>
 * The warning message is stored as a string including the priority level. The
 * {@link #warnings} list stores the strings in no particular order. The full
 * string can be: "<i>AdvancedConfig.yml contains incorrect groups!>10</i>". The
 * '>' identifier splits the real warning message and the priority. If this
 * identifier is not found, the priority will be set to 1.
 * 
 * <p>
 * <b>NOTE:</b> <br>
 * Warnings cannot be unregistered. The only way to remove the warnings is to
 * fix them so they don't ever get registered.
 * 
 * @author Staartvin
 * 
 */
public class WarningManager {

	private final Autorank plugin;

	public WarningManager(final Autorank plugin) {
		this.plugin = plugin;
	}

	private final HashMap<String, Integer> warnings = new HashMap<String, Integer>();

	private String findHighestPriorityWarning() {
		String highestWarning = null;
		int highestPriority = 0;

		for (final Entry<String, Integer> entry : warnings.entrySet()) {

			if (entry.getValue() > highestPriority) {
				highestPriority = entry.getValue();
				highestWarning = entry.getKey();
			}
		}

		return highestWarning;
	}

	/**
	 * Returns warning with the highest priority level.
	 * 
	 * @return highest priority warning message; returns null if no warning was
	 *         found
	 */
	public String getHighestWarning() {

		final String highestWarning = findHighestPriorityWarning();

		if (highestWarning == null) {
			return null;
		}

		// Return the highest one
		return highestWarning;
	}

	/**
	 * Register a new warning.
	 * Priority level will be capped if this number is too high or too low.
	 * 
	 * @param message Warning message description
	 * @param priority Priority level (1 - 10).
	 */
	public void registerWarning(final String message, int priority) {
		// Set priority limits
		if (priority > 10) {
			priority = 10;
		} else if (priority < 1) {
			priority = 1;
		}

		warnings.put(message, priority);
	}

	public void startWarningTask() {
		// Create a new task that runs every 30 seconds (will show a warning every 30 seconds)
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new WarningNoticeTask(plugin), 0,
				30L * 20L);
	}
}
