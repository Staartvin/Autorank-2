package me.armar.plugins.autorank.config;

import com.google.common.collect.Lists;
import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to talk to the Paths.yml file. It allows creation,
 * deleting and saving of the Paths.yml. <br>
 * It is used to retrieve properties of paths, whether it may be requirements,
 * prerequisites or results.
 *
 * @author Staartvin
 */
public class PathsConfig extends AbstractConfig {

    public PathsConfig(Autorank instance) {
        setPlugin(instance);
        setFileName("Paths.yml");
    }

    /**
     * Check whether a path can be completed more than once.
     *
     * @param pathName Name of path
     * @return true if a player can do a path multiple times, false otherwise.
     */
    public boolean isPathRepeatable(String pathName) {
        boolean allowInfinitePathing = this.getConfig().getBoolean(pathName + ".options.infinite pathing", false);

        boolean defaultValue = this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(
                DefaultBehaviorOption.ALLOW_INFINITE_PATHING);

        boolean isRepeatable = this.getConfig().getBoolean(pathName + ".options.is repeatable", false);

        if (allowInfinitePathing || isRepeatable) {
            return true;
        }

        return defaultValue;
    }

    /**
     * Get the display name of this path. Will return the regular path name if
     * no display name was specified.
     *
     * @param pathName Name of the path
     * @return display name of the path or the path name itself if it doesn't
     * exist.
     */
    public String getDisplayName(final String pathName) {
        return this.getConfig().getString(pathName + ".options.display name", pathName);
    }

    /**
     * Get a list of paths that are defined in the Paths.yml
     *
     * @return a list of path names.
     */
    public List<String> getPaths() {
        return new ArrayList<>(getConfig().getKeys(false));
    }

    /**
     * Get the ID of a requirement for a certain path
     *
     * @param pathName Name of the path
     * @param reqName  Name of the requirement
     * @return requirement id or -1 if none was found.
     */
    public int getReqId(final String pathName, final String reqName, boolean isPreRequisite) {
        final Object[] reqs = getRequirements(pathName, isPreRequisite).toArray();

        for (int i = 0; i < reqs.length; i++) {
            final String reqString = (String) reqs[i];

            if (reqName.equalsIgnoreCase(reqString)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Get the name of a requirement that corresponds to the given requirement id.
     *
     * @param pathName Name of the path.
     * @param reqId    Id of the requirement
     * @return Name of the requirement that matches this id, or null if it doesn't exist.
     */
    public String getRequirementName(String pathName, int reqId, boolean isPreRequisite) {
        List<String> reqs = this.getRequirements(pathName, isPreRequisite);

        if (reqId < 0 || reqId >= reqs.size()) {
            return null;
        }

        return reqs.get(reqId);
    }

    /**
     * Get the value strings for the specified requirement. Most of the times,
     * this will be just a single value string. <br>
     * These value strings are used to initialise the paths.
     *
     * @param pathName Name of the path
     * @param reqName  Name of the requirement
     * @return a list of value strings or an empty list.
     */
    public List<String[]> getRequirementOptions(final String pathName, final String reqName, boolean isPreRequisite) {
        // Grab options from string
        final String org = this.getRequirementValue(pathName, reqName, isPreRequisite);

        final List<String[]> list = new ArrayList<>();

        final String[] split = org.split(",");

        for (final String sp : split) {
            final StringBuilder builder = new StringBuilder(sp);

            if (builder.charAt(0) == '(') {
                builder.deleteCharAt(0);
            }

            if (builder.charAt(builder.length() - 1) == ')') {
                builder.deleteCharAt(builder.length() - 1);
            }

            final String[] splitArray = builder.toString().trim().split(";");
            list.add(splitArray);
        }

        return list;
    }

    /**
     * Get a list of requirements for a specific path.
     *
     * @param pathName Name of path
     * @return a list of all the names of the requirements.
     */
    public List<String> getRequirements(String pathName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        ConfigurationSection section = getConfig().getConfigurationSection(pathName + "." + keyType);

        if (section == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(section.getKeys(false));
    }

    /**
     * Get the value string that is associated with the given requirement for a
     * given path. <br>
     * This is used with {@link #getRequirementOptions(String, String, boolean)}}.
     *
     * @param pathName Name of the path.
     * @param reqName  Name of the requirement.
     * @return the value string which can be null if none is specified.
     */
    private String getRequirementValue(final String pathName, final String reqName, boolean isPreRequisite) {

        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        // Check if there is a value for PathName.requirements.RequirementName.Value
        // If not, use PathName.requirements.RequirementName (without the Value).

        return (this.getConfig().get(pathName + "." + keyType + "." + reqName + ".value") != null)
                ? this.getConfig().get(pathName + "." + keyType + "." + reqName + ".value").toString()
                : this.getConfig().getString(pathName + "." + keyType + "." + reqName);
    }

    /**
     * Get the result string for a specific result of a path.
     *
     * @param pathName   Name of path
     * @param resultName Name of the result
     * @return the value string or null if it doesn't exist.
     */
    public String getResultOfPath(String pathName, String resultName) {
        // Correct config
        String result;
        result = (this.getConfig().get(pathName + ".results." + resultName + ".value") != null)
                ? this.getConfig().get(pathName + ".results." + resultName + ".value").toString()
                : this.getConfig().getString(pathName + ".results." + resultName);

        return result;
    }

    /**
     * Get the result string for a specific result of a specific requirement of
     * a path.
     *
     * @param pathName Name of the path
     * @param reqName  Name of the requirement
     * @param resName  Name of the result
     * @return the value string or null if it doesn't exist.
     */
    public String getResultOfRequirement(final String pathName, final String reqName, final String resName, boolean
            isPreRequisite) {

        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        // Check if there is a value for PathName.requirements.RequirementName.results.ResultName.Value
        // If not, use PathName.requirements.RequirementName.results.ResultName (without the Value).

        return (this.getConfig().get(pathName + "." + keyType + "." + reqName + ".results." + resName + "" +
                ".value") != null)
                ? this.getConfig().get(pathName + "." + keyType + "." + reqName + ".results." + resName + ".value")
                .toString()
                : this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".results." + resName);
    }

    /**
     * Get all results of a path.
     *
     * @param pathName Name of path
     * @return a list of names as results.
     */
    public List<String> getResults(String pathName) {
        return new ArrayList<>(getConfig().getConfigurationSection(pathName + ".results").getKeys(false));
    }

    /**
     * Get all results names for a specific requirement of a path.
     *
     * @param pathName Name of path
     * @param reqName  Name of requirement
     * @return a list of names that correspond with results or an empty list.
     */
    public List<String> getResultsOfRequirement(final String pathName, final String reqName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        Set<String> results = (getConfig().getConfigurationSection(pathName + "." + keyType + "." + reqName + "" +
                ".results") != null)
                ? getConfig().getConfigurationSection(pathName + "." + keyType + "." + reqName + ".results").getKeys
                (false)
                : new HashSet<String>();

        return Lists.newArrayList(results);
    }

    /**
     * Get the value of the 'world' option. This option is used to specify
     * whether a requirement should hold on a given world.
     *
     * @param pathName Name of path
     * @param reqName  Name of the requirement
     * @return the value string of the 'world' option, or null if it doesn't
     * exist.
     */
    public String getWorldOfRequirement(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        return this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".options.world", null);
    }

    /**
     * Get whether a requirement is optional for a certain path
     *
     * @param pathName Name of path
     * @param reqName  Name of requirement
     * @return true if optional; false otherwise
     */
    public boolean isOptionalRequirement(final String pathName, final String reqName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        return getConfig().getBoolean(pathName + "." + keyType + "." + reqName + ".options.optional",
                this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                        .IS_OPTIONAL_REQUIREMENT));
    }

    /**
     * Get whether a requirement is world specific. For more info, see
     * {@link #getWorldOfRequirement(String, String, boolean)}.
     *
     * @param pathName Name of path
     * @param reqName  Name of requirement
     * @return true if the given requirement has a 'world' option. False
     * otherwise.
     */
    public boolean isRequirementWorldSpecific(String pathName, String reqName, boolean isPreRequisite) {
        return this.getWorldOfRequirement(pathName, reqName, isPreRequisite) != null;
    }

    /**
     * Get whether Autorank should auto complete for a certain requirement in a
     * path. <br>
     * If auto completion is turned on for a requirement, Autorank will mark it
     * as done when it detects that a player meets the requirement. Autorank
     * will also perform the results of that requirement.
     *
     * @param pathName Name of path
     * @param reqName  Name of requirement
     * @return true if auto completion is turned on, false otherwise.
     */
    public boolean useAutoCompletion(final String pathName, final String reqName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        // If the option is not defined, use default value of auto complete.
        if (this.getConfig().get(pathName + "." + keyType + "." + reqName + ".options.auto complete") == null) {
            return this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                    .AUTO_COMPLETE_REQUIREMENT);
        } else {
            // Else, return defined value
            return this.getConfig().getBoolean(pathName + "." + keyType + "." + reqName + ".options.auto complete");
        }
    }

    /**
     * Get the results that should be performed when a user chooses a given path.
     *
     * @param pathName Name of the path
     * @return a list of result names that should be used performed
     */
    public ArrayList<String> getResultsUponChoosing(String pathName) {
        Set<String> results = (getConfig().getConfigurationSection(pathName + ".upon choosing") != null)
                ? getConfig().getConfigurationSection(pathName + ".upon choosing").getKeys(false)
                : new HashSet<String>();

        return Lists.newArrayList(results);
    }

    /**
     * Get the result value of the given result.
     *
     * @param pathName Name of the path
     * @param resName  Name of the result
     * @return the string value of this result or null if it doesn't exist.
     */
    public String getResultValueUponChoosing(final String pathName, final String resName) {
        // Correct config
        String result;
        result = (this.getConfig().get(pathName + ".upon choosing." + resName + ".value") != null)
                ? this.getConfig().get(pathName + ".upon choosing." + resName + ".value").toString()
                : this.getConfig().getString(pathName + ".upon choosing." + resName);

        return result;
    }

    /**
     * Check whether Autorank should automatically assign the given path to a player when he meets the prerequisites.
     *
     * @param pathName Name of the path
     * @return true if Autorank should assign the given path to the player, false otherwise.
     */
    public boolean shouldAutoAssignPath(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.auto choose",
                this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                        .AUTO_CHOOSE_PATH));
    }

    /**
     * Get the priority of a given path. The priority of a path is used to determine which path Autorank
     * should automatically assign to a player. The priority is a positive integer (unbounded). By default, all paths
     * have a priority of 1.
     *
     * @param pathName Name of the path
     * @return a positive integer representing the priority of the path. By default the priority is 1.
     */
    public int getPriorityOfPath(String pathName) {
        return this.getConfig().getInt(pathName + ".options.priority",
                this.getPlugin().getDefaultBehaviorConfig().getDefaultIntegerBehaviorOfOption(DefaultBehaviorOption
                        .PRIORITY_PATH));
    }

    /**
     * Check whether Autorank should show the given path in the list of possible paths based on whether a player
     * meets the prerequisites of the path.
     *
     * @param pathName Name of the path
     * @return true if Autorank should only show the given path if the player meets the path's prerequisites, false
     * otherwise.
     */
    public boolean showBasedOnPrerequisites(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.show based on prerequisites", this.getPlugin()
                .getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                        .SHOW_PATH_BASED_ON_PREREQUISITES));
    }

    /**
     * Check whether a player on a given path can complete requirements one by one, or if he should meet all
     * requirements at the same time to complete the path.
     *
     * @param pathName Name of the path
     * @return true if the player can complete requirements one by one. False if all requirements need to be met
     * simultaneously.
     */
    public boolean isPartialCompletionAllowed(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.allow partial completion",
                this.getPlugin().getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                        .ALLOW_PARTIAL_COMPLETION));
    }

    /**
     * Get the description of a path
     *
     * @param pathName Name of the path
     * @return a description (if provided) or an empty string.
     */
    public String getPathDescription(String pathName) {
        return this.getConfig().getString(pathName + ".options.description", "");
    }

    /**
     * Check whether a requirement has a custom description.
     *
     * @param pathName       Name of the path where the requirement resides in
     * @param reqName        Name of the requirement
     * @param isPreRequisite whether the requirement is a prerequisite or not.
     * @return true if the given requirement has a custom requirement.
     */
    public boolean hasCustomRequirementDescription(String pathName, String reqName, boolean isPreRequisite) {
        return this.getCustomRequirementDescription(pathName, reqName, isPreRequisite) != null;
    }

    /**
     * Get the custom description of a requirement as set in the paths file.
     *
     * @param pathName       Name of the path the requirement is part of.
     * @param reqName        Name of the requirement.
     * @param isPreRequisite Whether the requirement is a prerequisite or not.
     * @return the custom description or null if there is no custom description.
     */
    public String getCustomRequirementDescription(String pathName, String reqName, boolean isPreRequisite) {
        String keyType = (isPreRequisite ? "prerequisites" : "requirements");

        return this.getConfig().getString(pathName + "." + keyType + "." + reqName + ".options.description", null);
    }

    /**
     * Check whether a result has a custom description.
     *
     * @param pathName Name of the path where the result resides in
     * @param resName  Name of the requirement
     * @return true if the given result has a custom description.
     */
    public boolean hasCustomResultDescription(String pathName, String resName) {
        return this.getCustomResultDescription(pathName, resName) != null;
    }

    /**
     * Get the custom description of a result as set in the paths file.
     *
     * @param pathName Name of the path the result is part of.
     * @param resName  Name of the result.
     * @return the custom description or null if there is no custom description.
     */
    public String getCustomResultDescription(String pathName, String resName) {
        return this.getConfig().getString(pathName + ".results." + resName + ".options.description",
                null);
    }

    /**
     * Check whether Autorank should store the progress of a player's path when he deactivates a path. If the
     * progress is stored and the player reactivates the path, he can start where he left off.
     *
     * @param pathName Name of the path
     * @return true if progress should be stored, false otherwise.
     */
    public boolean shouldStoreProgressOnDeactivation(String pathName) {
        return this.getConfig().getBoolean(pathName + ".options.store progress on deactivation", this.getPlugin()
                .getDefaultBehaviorConfig().getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption
                        .STORE_PROGRESS_ON_DEACTIVATION));
    }
}
