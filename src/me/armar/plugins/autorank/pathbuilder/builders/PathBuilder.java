package me.armar.plugins.autorank.pathbuilder.builders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;

import java.util.ArrayList;
import java.util.List;

/**
 * This class builds all the paths from the paths.yml. <br>
 * <p>
 * Date created: 14:20:20 5 aug. 2015
 *
 * @author Staartvin
 */
public class PathBuilder {

    private final Autorank plugin;

    public PathBuilder(final Autorank plugin) {
        this.plugin = plugin;
    }

    /**
     * Return a list of paths that have been defined in the Paths.yml file.
     *
     * @return a list of paths or an empty list.
     */
    public List<Path> initialisePaths() {
        List<Path> paths = new ArrayList<>();

        // If the paths file could not be loaded, we don't start reading paths.
        if (!plugin.getPathsConfig().isLoaded()) {
            return paths;
        }

        for (String pathName : plugin.getPathsConfig().getPaths()) {

            // Add a path object to this pathName
            Path path = new Path(plugin);

            // Set internal name
            path.setInternalName(pathName);

            // Initialize results
            for (AbstractResult result : getResults(pathName)) {
                path.addResult(result);
            }

            // Initialize prerequisites.
            for (CompositeRequirement prerequisite : getPrerequisites(pathName)) {
                path.addPrerequisite(prerequisite);
            }

            // Initialize requirements.
            for (CompositeRequirement requirement : getRequirements(pathName)) {
                path.addRequirement(requirement);
            }

            // Initialize results upon choosing.
            for (AbstractResult result : getResultsUponChoosing(pathName)) {
                path.addResultUponChoosing(result);
            }

            // Now add display name to this path.
            path.setDisplayName(plugin.getPathsConfig().getDisplayName(pathName));

            // Set description
            path.setDescription(plugin.getPathsConfig().getPathDescription(pathName));

            // Set whether a path is repeatable.
            path.setRepeatable(plugin.getPathsConfig().isPathRepeatable(pathName));

            // Set whether a path is automatically assigned to a player or not.
            path.setAutomaticallyAssigned(plugin.getPathsConfig().shouldAutoAssignPath(pathName));

            // Set whether partial completion is allowed.
            path.setAllowPartialCompletion(plugin.getPathsConfig().isPartialCompletionAllowed(pathName));

            // Set whether this path can be seen by players if they do not meet the prerequisites
            path.setOnlyShowIfPrerequisitesMet(plugin.getPathsConfig().showBasedOnPrerequisites(pathName));

            // Set whether the progress for this path should be stored.
            path.setStoreProgressOnDeactivation(plugin.getPathsConfig().shouldStoreProgressOnDeactivation(pathName));

            // Add path to list of paths
            paths.add(path);
        }

        return paths;
    }

    private List<CompositeRequirement> getPrerequisites(String pathName) {
        List<CompositeRequirement> prerequisites = new ArrayList<>();

        // Lastly, initialize pre-requisites
        for (String preReqName : plugin.getPathsConfig().getRequirements(pathName, true)) {

            // Create a holder for the path
            final CompositeRequirement reqHolder = new CompositeRequirement(plugin);

            // Option strings separated
            final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, preReqName,
                    true);

            // Find all options of this prerequisites
            for (final String[] options : optionsList) {
                final AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, preReqName,
                        options, true);

                if (requirement == null) {
                    continue;
                }

                // Add prerequisite to holder
                reqHolder.addRequirement(requirement);
            }

            if (reqHolder.getRequirements().isEmpty()) {
                continue;
            }

            prerequisites.add(reqHolder);
        }

        return prerequisites;
    }

    private List<CompositeRequirement> getRequirements(String pathName) {
        List<CompositeRequirement> requirements = new ArrayList<>();

        // Now initialize requirements
        for (final String reqName : plugin.getPathsConfig().getRequirements(pathName, false)) {

            // Create a holder for the path
            final CompositeRequirement reqHolder = new CompositeRequirement(plugin);

            // Option strings separated
            final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, reqName, false);

            // Find all options of this requirement
            for (final String[] options : optionsList) {
                final AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, reqName,
                        options, false);

                if (requirement == null) {
                    continue;
                }

                // Add requirement to holder
                reqHolder.addRequirement(requirement);
            }

            if (reqHolder.getRequirements().isEmpty()) {
                continue;
            }

            // Now add holder to requirement list of path
            requirements.add(reqHolder);
        }

        return requirements;
    }

    private List<AbstractResult> getResults(String pathName) {
        List<AbstractResult> results = new ArrayList<>();

        for (String resultName : plugin.getPathsConfig().getResults(pathName)) {
            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultName, plugin
                    .getPathsConfig().getResultOfPath(pathName, resultName));

            if (abstractResult == null) {
                continue;
            }

            // Add abstractResult to path
            results.add(abstractResult);
        }

        return results;
    }

    private List<AbstractResult> getResultsUponChoosing(String pathName) {
        List<AbstractResult> resultsUponChoosing = new ArrayList<>();

        // AbstractResult for this path (upon choosing)
        final List<String> results = plugin.getPathsConfig().getResultsUponChoosing(pathName);

        // Get results of requirement
        for (final String resultType : results) {

            AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultType,
                    plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultType));

            if (abstractResult == null) {
                continue;
            }

            resultsUponChoosing.add(abstractResult);
        }

        return resultsUponChoosing;
    }
}
