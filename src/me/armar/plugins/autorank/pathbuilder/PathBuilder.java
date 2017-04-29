package me.armar.plugins.autorank.pathbuilder;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

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

        for (String pathName : plugin.getPathsConfig().getPaths()) {
            // Add a path object to this pathName
            Path path = new Path(plugin);

            // First initialize results
            for (String resultName : plugin.getPathsConfig().getResults(pathName)) {
                Result result = ResultBuilder.createResult(pathName, resultName, plugin.getPathsConfig().getResultOfPath(pathName, resultName));

                if (result == null) {
                    continue;
                }

                // Add result to path
                path.addResult(result);
            }

            // Now initialize requirements
            for (final String reqName : plugin.getPathsConfig().getRequirements(pathName, false)) {

                // Create a holder for the path
                final RequirementsHolder reqHolder = new RequirementsHolder(plugin);

                // Option strings separated
                final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, reqName, false);

                // Find all options of this requirement
                for (final String[] options : optionsList) {
                    final Requirement requirement = RequirementBuilder.createRequirement(pathName, reqName, options, false);

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
                path.addRequirement(reqHolder);
            }

            // Lastly, initialize pre-requisites
            for (String preReqName : plugin.getPathsConfig().getRequirements(pathName, true)) {

                // Create a holder for the path
                final RequirementsHolder reqHolder = new RequirementsHolder(plugin);

                // Option strings separated
                final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, preReqName, true);

                // Find all options of this prerequisites
                for (final String[] options : optionsList) {
                    final Requirement requirement = RequirementBuilder.createRequirement(pathName, preReqName, options, true);

                    if (requirement == null) {
                        continue;
                    }

                    // Add prerequisite to holder
                    reqHolder.addRequirement(requirement);
                }

                if (reqHolder.getRequirements().isEmpty()) {
                    continue;
                }

                // Now add holder to prerequisites list of path
                path.addPrerequisite(reqHolder);

            }

            // Result for this path (upon choosing)
            final List<String> results = plugin.getPathsConfig().getResultsUponChoosing(pathName);

            // Create a new result List that will get all result when this
            // path is chosen
            final List<Result> realResults = new ArrayList<>();

            // Get results of requirement
            for (final String resultType : results) {

                Result result = ResultBuilder.createResult(pathName, resultType,
                        plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultType));

                if (result == null) {
                    continue;
                }

                realResults.add(result);
            }

            // Now set the result upon choosing for this path
            path.setResultsUponChoosing(realResults);

            // Now add display name to this path.
            path.setDisplayName(plugin.getPathsConfig().getDisplayName(pathName));

            // Set internal name
            path.setInternalName(pathName);

            // Add path to list of paths
            paths.add(path);
        }

        return paths;
    }
}
