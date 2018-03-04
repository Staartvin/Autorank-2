package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
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

        for (String pathName : plugin.getPathsConfig().getPaths()) {
            // Add a path object to this pathName
            Path path = new Path(plugin);

            // First initialize results
            for (String resultName : plugin.getPathsConfig().getResults(pathName)) {
                AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultName, plugin
                        .getPathsConfig().getResultOfPath(pathName, resultName));

                if (abstractResult == null) {
                    continue;
                }

                // Add abstractResult to path
                path.addResult(abstractResult);
            }

            // Now initialize requirements
            for (final String reqName : plugin.getPathsConfig().getRequirements(pathName, false)) {

                // Create a holder for the path
                final RequirementsHolder reqHolder = new RequirementsHolder(plugin);

                // Option strings separated
                final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, reqName, false);

                // Find all options of this requirement
                for (final String[] options : optionsList) {
                    final AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, reqName, options, false);

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
                    final AbstractRequirement requirement = RequirementBuilder.createRequirement(pathName, preReqName, options, true);

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

            // AbstractResult for this path (upon choosing)
            final List<String> results = plugin.getPathsConfig().getResultsUponChoosing(pathName);

            // Create a new result List that will get all result when this
            // path is chosen
            final List<AbstractResult> realAbstractResults = new ArrayList<>();

            // Get results of requirement
            for (final String resultType : results) {

                AbstractResult abstractResult = ResultBuilder.createResult(pathName, resultType,
                        plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultType));

                if (abstractResult == null) {
                    continue;
                }

                realAbstractResults.add(abstractResult);
            }

            // Now set the result upon choosing for this path
            path.setResultsUponChoosing(realAbstractResults);

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
