package me.armar.plugins.autorank.config;

import com.google.common.collect.Lists;
import java.util.*;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;

/**
 * This class handles has all methods to get data from the configs. This is now handled by every
 * class seperately, but should be organised soon.
 *
 * @author Staartvin
 *
 */
public class ConfigHandler {

    public enum MySQLOptions {

        DATABASE,
        HOSTNAME,
        PASSWORD,
        TABLE,
        USERNAME
    }

    private final Autorank plugin;

    public ConfigHandler(final Autorank instance) {
        plugin = instance;
    }

    public boolean doBaseHelpPageOnPermission() {
        return plugin.getSettingsConfig().getBoolean(
                "show help command based on permission", false);
    }

    public boolean doCheckForNewerVersion() {
        return plugin.getSettingsConfig().getBoolean(
                "auto-updater.check-for-new-versions", true);
    }

    public String getCheckCommandLayout() {
        return plugin
                .getSettingsConfig()
                .getString(
                        "check command layout",
                        "&p has played for &time and is in group(s) &groups. Requirements to be ranked up: &reqs");
    }

    public int getIntervalTime() {
        return plugin.getSettingsConfig().getInt("interval check", 5);
    }

    public String getLeaderboardLayout() {
        return plugin.getSettingsConfig().getString("leaderboard layout",
                "&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
    }

    public int getLeaderboardLength() {
        return plugin.getSettingsConfig().getInt("leaderboard length", 10);
    }

    public String getMySQLSettings(final MySQLOptions option) {
        switch (option) {
            case HOSTNAME:
                return plugin.getSettingsConfig().getString("sql.hostname");
            case USERNAME:
                return plugin.getSettingsConfig().getString("sql.username");
            case PASSWORD:
                return plugin.getSettingsConfig().getString("sql.password");
            case DATABASE:
                return plugin.getSettingsConfig().getString("sql.database");
            case TABLE:
                return plugin.getSettingsConfig().getString("sql.table");
            default:
                return null;
        }
    }

    public String getRankChange(final String group) {
        return plugin.getAdvancedConfig().getString(
                "ranks." + group + ".results.rank change");
    }

    public Set<String> getRanks() {
        return plugin.getAdvancedConfig().getConfigurationSection("ranks")
                .getKeys(false);
    }

    /**
     * Gets the requirement's id.
     *
     * @param requirement Requirement name exactly as it is in the config
     * @param group Group the requirement is from
     * @return requirement id, -1 if nothing found
     */
    public int getReqId(final String requirement, final String group) {
        final Object[] reqs = getRequirements(group).toArray();

        for (int i = 0; i < reqs.length; i++) {
            final String req2 = (String) reqs[i];

            if (requirement.equalsIgnoreCase(req2)) {
                return i;
            }
        }

        return -1;
    }

    public String getRequirement(final String requirement, final String group) {

        // Correct config
        String result;
        result = (plugin.getAdvancedConfig().get(
                "ranks." + group + ".requirements." + requirement + ".value") != null) ? plugin
                        .getAdvancedConfig()
                        .get("ranks." + group + ".requirements." + requirement
                                + ".value").toString()
                        : plugin.getAdvancedConfig()
                        .getString(
                                "ranks." + group + ".requirements."
                                + requirement).toString();

        return result;
    }

    public Set<String> getRequirements(final String group) {
        Set<String> requirements;
        try {
            requirements = plugin
                    .getAdvancedConfig()
                    .getConfigurationSection("ranks." + group + ".requirements")
                    .getKeys(false);
        } catch (final NullPointerException e) {
            plugin.getLogger().severe(
                    "Error occured when trying to get requirements for group '"
                    + group + "'!");
            return Collections.emptySet();
        }

        return requirements;
    }

    public String getResult(final String result, final String group) {
        return plugin.getAdvancedConfig()
                .get("ranks." + group + ".results." + result).toString();
    }

    public String getResultOfRequirement(final String requirement,
            final String group, final String result) {
        return plugin
                .getAdvancedConfig()
                .get("ranks." + group + ".requirements." + requirement
                        + ".results." + result).toString();
    }

    public Set<String> getResults(final String group) {
        final Set<String> results = plugin.getAdvancedConfig()
                .getConfigurationSection("ranks." + group + ".results")
                .getKeys(false);

        return results;
    }

    public List<String> getResultsOfRequirement(final String requirement,
            final String group) {
        Set<String> results = new HashSet<String>();

        results = (plugin.getAdvancedConfig().getConfigurationSection(
                "ranks." + group + ".requirements." + requirement + ".results") != null) ? plugin
                        .getAdvancedConfig()
                        .getConfigurationSection(
                                "ranks." + group + ".requirements." + requirement
                                + ".results").getKeys(false)
                        : new HashSet<String>();

        return Lists.newArrayList(results);
    }

    /**
     * Gets whether a requirement is optional for a certain group
     *
     * @param requirement
     * @param group
     * @return true if optional; false otherwise
     */
    public boolean isOptional(final String requirement, final String group) {
        final boolean optional = plugin.getAdvancedConfig().getBoolean(
                "ranks." + group + ".requirements." + requirement
                + ".options.optional", false);

        return optional;
    }

    public boolean useAdvancedConfig() {
        return plugin.getSettingsConfig().getBoolean("use advanced config");
    }

    /**
     * Check whether Autorank should log detailed information about <br>
     * the found dependencies.
     *
     * @return true if has to, false otherwise.
     */
    public boolean useAdvancedDependencyLogs() {
        return plugin.getSettingsConfig().getBoolean(
                "advanced dependency output", false);
    }

    /**
     * Whether Autorank should care about players that are AFK or not. <br>
     * If the SimpleConfig is used, this will always be true.
     *
     * @return true when AFK integration should be used; false otherwise.
     */
    public boolean useAFKIntegration() {
        return plugin.getSettingsConfig().getBoolean("afk integration", false);
    }

    public boolean useAutoCompletion(final String group,
            final String requirement) {
        final boolean optional = isOptional(requirement, group);

        if (optional) {
            // Not defined (Optional + not defined = false)
            if (plugin.getAdvancedConfig().get(
                    "ranks." + group + ".requirements." + requirement
                    + ".options.auto complete") == null) {
                //System.out.print("Return false for " + group + " requirement " + requirement);
                return false;
            } else {
                // Defined (Optional + defined = defined)
                //System.out.print("Return defined for " + group + " requirement " + requirement);
                return plugin.getAdvancedConfig().getBoolean(
                        "ranks." + group + ".requirements." + requirement
                        + ".options.auto complete");
            }
        } else {
            // Not defined (Not optional + not defined = true)
            if (plugin.getAdvancedConfig().get(
                    "ranks." + group + ".requirements." + requirement
                    + ".options.auto complete") == null) {

                // If partial completion is false, we do not auto complete
                if (!usePartialCompletion()) {
                    return false;
                }
                //System.out.print("Return true for " + group + " requirement " + requirement);
                return true;
            } else {
                // Defined (Not optional + defined = defined)
                //System.out.print("Return defined for " + group + " requirement " + requirement);
                return plugin.getAdvancedConfig().getBoolean(
                        "ranks." + group + ".requirements." + requirement
                        + ".options.auto complete");
            }
        }
    }

    public boolean useDebugOutput() {
        return plugin.getSettingsConfig().getBoolean("use debug", false);
    }

    public boolean useMySQL() {
        return plugin.getSettingsConfig().getBoolean("sql.enabled");
    }

    public boolean usePartialCompletion() {
        return plugin.getSettingsConfig().getBoolean("use partial completion",
                false);
    }

    /**
     * Get the plugin that is used to get the time a player played on this server. <br>
     * This is only accounted for the local time. The global time is still calculated by Autorank.
     *
     * @return {@link me.armar.plugins.autorank.hooks.DependencyManager.dependency} object that is
     * used
     */
    public dependency useTimeOf() {

        final String timePlugin = plugin.getSettingsConfig().getString(
                "use time of", "Autorank");

        if (timePlugin.equalsIgnoreCase("Stats")) {
            return dependency.STATS;
        } else if (timePlugin.equalsIgnoreCase("OnTime")) {
            return dependency.ONTIME;
        } else {
            return dependency.AUTORANK;
        }
    }

    public boolean allowInfiniteRanking() {
        return plugin.getSettingsConfig().getBoolean("allow infinite ranking",
                false);
    }

    public List<String[]> getOptions(final String requirement,
            final String group) {
        // Grab options from string
        final String org = this.getRequirement(requirement, group);

        final List<String[]> list = new ArrayList<String[]>();

        final String[] split = org.split(",");

        for (final String sp : split) {
            final String newString = sp.replace("(", "").replace(")", "")
                    .trim();
            final String[] splitArray = newString.split(";");
            list.add(splitArray);
        }

        return list;
    }

    public boolean showWarnings() {
        return plugin.getSettingsConfig().getBoolean("show warnings", true);
    }
}
