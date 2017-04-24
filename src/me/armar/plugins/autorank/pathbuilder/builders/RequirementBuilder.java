package me.armar.plugins.autorank.pathbuilder.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;

public class RequirementBuilder {

    private static final Map<String, Class<? extends Requirement>> reqs = new HashMap<String, Class<? extends Requirement>>();

    // Keep track of the associated requirement.
    private Requirement requirement = null;

    // Whether the associated requirement is valid.
    private boolean isValid = false;

    // Extra metadata for the associated requirement.
    private String pathName, requirementType;

    // Whether this requirement is a prerequisite.
    private boolean isPreRequisite = false;

    final String errorMessage = "Could not set up requirement '" + requirementType + "' of " + pathName
            + "! It's invalid: check the wiki for documentation.";

    /**
     * Create an empty Requirement.
     *
     * @param pathName        Name of the path that this requirement is in.
     * @param requirementType Type of the requirement.
     * @return this builder.
     */
    public RequirementBuilder createEmpty(String pathName, String requirementType, boolean isPreRequisite) {

        this.pathName = pathName;
        this.requirementType = requirementType;
        this.isPreRequisite = isPreRequisite;

        requirementType = AutorankTools.getCorrectReqName(requirementType);

        if (requirementType == null) {
            Autorank.getInstance().getWarningManager()
                    .registerWarning(String.format(
                            "You are using a '%s' requirement in path '%s', but that requirement doesn't exist!", requirementType,
                            pathName), 10);
            return this;
        }

        final Class<? extends Requirement> c = reqs.get(requirementType);
        if (c != null)
            try {
                requirement = c.newInstance();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        else {
            Bukkit.getServer().getConsoleSender().sendMessage(
                    "[Autorank] " + ChatColor.RED + "Requirement '" + requirementType + "' is not a valid requirement type!");
            return null;
        }
        return this;
    }

    /**
     * Populate the created Requirement with data.
     *
     * @return this builder.
     */
    public RequirementBuilder populateRequirement(String[] options) {

        if (requirement == null) {
            return this;
        }

        // Set Autorank instance.
        requirement.setAutorank(Autorank.getInstance());

        if (options == null) {
            return this;
        }

        // Initiliaze the result with options.
        if (!requirement.setOptions(options)) {
            Autorank.getInstance().getLogger().severe(errorMessage);
            Autorank.getInstance().getWarningManager().registerWarning(errorMessage, 10);
        }

        // Set whether requirement is optional or not.
        requirement.setOptional(Autorank.getInstance().getPathsConfig().isOptionalRequirement(pathName, requirementType, isPreRequisite));

        List<Result> resultList = new ArrayList<>();

        for (String resultType : Autorank.getInstance().getPathsConfig().getResultsOfRequirement(pathName, requirementType, isPreRequisite)) {
            Result result = ResultBuilder.createResult(pathName, resultType,
                    Autorank.getInstance().getPathsConfig().getResultOfRequirement(pathName, requirementType, resultType, isPreRequisite));

            if (result == null) {
                continue;
            }

            resultList.add(result);
        }

        // Set results of requirement.
        requirement.setResults(resultList);

        // Set whether this requirement should auto complete.
        requirement.setAutoComplete(Autorank.getInstance().getPathsConfig().useAutoCompletion(pathName, requirementType, isPreRequisite));

        int requirementId = Autorank.getInstance().getPathsConfig().getReqId(pathName, requirementType, isPreRequisite);

        // Do sanity check
        if (requirementId < 0) {
            throw new IllegalStateException("Requirement ID of a requirement could not be found. This means there is something wrong with your configuration." +
                    " Path: " + pathName
                    + ", Requirement: " + requirementType);
        }

        // Set ID of the requirement
        requirement.setId(requirementId);

        // Set whether this requirement is world-specific.
        requirement.setWorld(Autorank.getInstance().getPathsConfig().getWorldOfRequirement(pathName, requirementType, isPreRequisite));

        // Result is non-null and populated with data, so valid.
        isValid = true;

        return this;
    }

    /**
     * Finish the creation of the Result, will return the result object that was created.
     *
     * @return created Result object.
     * @throws IllegalStateException if the result was not valid and could not be finished.
     */
    public Requirement finish() throws IllegalStateException {
        if (!isValid || requirement == null) {
            throw new IllegalStateException("Result '" + requirementType + "' of '" + pathName + "' was not valid" +
                    " and could not be finished.");
        }

        return requirement;
    }

    /**
     * Check whether the associated result is valid.
     *
     * @return true if it is, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }


    /**
     * Add a new type of Requirement that can be used in the Paths.yml file.
     *
     * @param type        String literal that must be used in the file to identify the requirement.
     * @param requirement Class of the Requirement that must be instantiated.
     */
    public static void registerRequirement(final String type, final Class<? extends Requirement> requirement) {
        // Add type to the list
        reqs.put(type, requirement);

        // Add type to the list of AutorankTools so it can use the correct name.
        AutorankTools.registerRequirement(type);
    }

    /**
     * Create a Requirement using the RequirementBuilder factory.
     * @param pathName Name of the path the requirement is in.
     * @param requirementType Type of the requirement, which does not have to be the exact string value.
     * @param options The requirements options array.
     * @return a newly created Requirement with the given data, or null if invalid data was given.
     */
    public static Requirement createRequirement(String pathName, String requirementType, String[] options, boolean isPreRequisite) {
        RequirementBuilder builder = new RequirementBuilder().createEmpty(pathName, requirementType, isPreRequisite).populateRequirement(options);

        // Check if requirement is valid before building it.
        if (!builder.isValid()) {
            return null;
        }

        // Get requirement of RequirementBuilder.
        final Requirement requirement = builder.finish();

        return requirement;
    }

}
