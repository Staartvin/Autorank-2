package me.armar.plugins.autorank.warningmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class handles all warning message that are displayed when something is
 * wrong with Autorank. Instead of disabling Autorank, it should display
 * warnings when an admin joins.
 * <p>
 * <p>
 * Warnings will be stored in a list. Every warning will have its own priority.
 * Whenever an player joins that has the permission '
 * <i>autorank.warning.notice</i>', the warning manager will grab the warning
 * with the highest priority and show it to the player. This way the admin knows
 * something is up and he has to fix it.
 * <p>
 * <p>
 * The highest priority warning will be saved as a message so it doesn't have to
 * be recalculated every time someone joins. It will be determined when the
 * first player that has the correct permission joins.
 * <p>
 * <p>
 * The priority level goes from <b>1 to 10</b>, with 1 being the lowest priority
 * and thus no need for attention, and 10 the highest, which represents critical
 * failures.
 * <p>
 * <p>
 * <b>NOTE:</b> <br>
 * Warnings cannot be unregistered. The only way to remove the warnings is to
 * fix them so they don't ever get registered.
 *
 * @author Staartvin
 */
public class WarningManager {

    private final Autorank plugin;

    private final HashMap<String, Integer> warnings = new HashMap<String, Integer>();

    public WarningManager(final Autorank plugin) {
        this.plugin = plugin;
    }

    public static final int LOW_PRIORITY_WARNING = 1;
    public static final int MEDIUM_PRIORITY_WARNING = 5;
    public static final int HIGH_PRIORITY_WARNING = 10;

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
     * found
     */
    public String getHighestWarning() {

        final String highestWarning = findHighestPriorityWarning();

        // Return the highest one
        return highestWarning;
    }

    /**
     * Register a new warning. Priority level will be capped if this number is
     * too high or too low.
     *
     * @param message  Warning message description
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
        // Create a new task that runs every 30 seconds (will show a warning
        // every 30 seconds)
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new WarningNoticeTask(plugin), 0,
                30 * AutorankTools.TICKS_PER_SECOND);
    }

    public HashMap<String, Integer> getWarnings() {
        return warnings;
    }

    public void sendWarnings(Player player) {
        for (Entry<String, Integer> warning : this.getWarnings().entrySet()) {

            String priorityString = "Low";

            int warningValue = warning.getValue();

            if (warningValue > 3 && warningValue < 7) {
                priorityString = "Medium";
            } else if (warningValue > 6) {
                priorityString = "High";
            }

            player.sendMessage(String.format(ChatColor.DARK_AQUA + "<Autorank warning> " + ChatColor.RED + "(%s " +
                    "priority): " + ChatColor.GREEN + "%s ", priorityString, warning.getKey()));

        }
    }
}
