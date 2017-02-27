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
 * 
 */
public class PathBuilder {

    private final Autorank plugin;

    private RequirementBuilder requirementBuilder;
    private ResultBuilder resultBuilder;

    public PathBuilder(final Autorank plugin) {
        this.plugin = plugin;
        setResultBuilder(new ResultBuilder());
        setRequirementBuilder(new RequirementBuilder());
    }

    private Requirement createPrerequisite(final String type, final String[] options, final boolean optional,
            final int reqId, String originalName, String originalGroup) {
        final Requirement res = requirementBuilder.create(type);

        if (res != null) {
            res.setAutorank(plugin);

            final String errorMessage = "Could not set up prerequisite '" + originalName + "' of " + originalGroup
                    + "! It's invalid: check the wiki for documentation.";

            try {
                if (!res.setOptions(options)) {
                    plugin.getLogger().severe(errorMessage);
                    plugin.getWarningManager().registerWarning(errorMessage, 10);
                }
            } catch (NoClassDefFoundError e) {
                plugin.getLogger().severe("You are using prerequisite " + originalName + " in " + originalGroup
                        + " but you don't have Statz installed! Make sure to install Statz for this to work.");
                plugin.getWarningManager().registerWarning(
                        "You are using prerequisite " + originalName + " in " + originalGroup
                                + " but you don't have Statz installed! Make sure to install Statz for this to work.",
                        10);
            } catch (final Exception e) {
                plugin.getLogger().severe(errorMessage);
                plugin.getWarningManager().registerWarning(errorMessage, 10);
            }

            res.setOptional(optional);
            res.setAutoComplete(true); // Prerequisite will always auto complete
            res.setReqId(reqId); // Set requirement ID
            res.setResults(new ArrayList<Result>()); // Empty results
        }
        return res;
    }

    private Requirement createRequirement(final String type, final String[] options, final boolean optional,
            final List<Result> results, final boolean autoComplete, final int reqId, String originalName,
            String originalGroup, String customDesc) {
        final Requirement res = requirementBuilder.create(type);

        if (res != null) {
            res.setAutorank(plugin);

            final String errorMessage = "Could not set up requirement '" + originalName + "' of " + originalGroup
                    + "! It's invalid: check the wiki for documentation.";

            try {
                if (!res.setOptions(options)) {
                    plugin.getLogger().severe(errorMessage);
                    plugin.getWarningManager().registerWarning(errorMessage, 10);
                }
            } catch (NoClassDefFoundError e) {
                plugin.getLogger().severe("You are using requirement " + originalName + " in " + originalGroup
                        + " but you don't have Statz installed! Make sure to install Statz for this to work.");
                plugin.getWarningManager().registerWarning(
                        "You are using requirement " + originalName + " in " + originalGroup
                                + " but you don't have Statz installed! Make sure to install Statz for this to work.",
                        10);
            } catch (final Exception e) {
                plugin.getLogger().severe(errorMessage);
                plugin.getWarningManager().registerWarning(errorMessage, 10);
            }
            
            if (customDesc != null) {
                res.setCustomDescription(customDesc);
            }

            res.setOptional(optional);
            res.setAutoComplete(autoComplete);
            res.setReqId(reqId);
            res.setResults(results);
        }
        return res;
    }

    private Result createResult(final String type, final String valueString) {
        final Result res = resultBuilder.create(type);

        if (res != null) {
            res.setAutorank(plugin);
            res.setOptions(valueString.split(";"));
        }

        return res;
    }

    public RequirementBuilder getRequirementBuilder() {
        return requirementBuilder;
    }

    public ResultBuilder getResultBuilder() {
        return resultBuilder;
    }

    public List<Path> initialisePaths() {
        List<String> pathNames = plugin.getPathsConfig().getPaths();

        List<Path> paths = new ArrayList<Path>();

        for (String pathName : pathNames) {
            // Add a path object to this pathName
            Path path = new Path(plugin);

            // First initialize results
            for (String resultName : plugin.getPathsConfig().getResults(pathName)) {
                final String correctResult = AutorankTools.getCorrectResName(resultName);

                if (correctResult == null) {
                    plugin.getWarningManager().registerWarning(
                            String.format("You are using a '%s' result in path '%s', but that result doesn't exist!", resultName,
                                    pathName),
                            10);
                    return null;
                    // throw new IllegalArgumentException(
                    // "Result '" + resultName + "' of group '" + pathName + "'
                    // is unknown!");
                }

                final Result result = createResult(correctResult,
                        plugin.getPathsConfig().getResultOfPath(pathName, resultName));

                if (result == null)
                    continue;

                // Add result to path
                path.addResult(result);
            }

            // Now initialize requirements
            for (final String reqName : plugin.getPathsConfig().getRequirements(pathName)) {

                // Implement optional option logic
                final boolean optional = plugin.getPathsConfig().isOptionalRequirement(pathName, reqName);
                // Result for requirements
                final List<String> results = plugin.getPathsConfig().getResultsOfRequirement(pathName, reqName);

                // Create a new result List that will get all result when this
                // requirement is met.
                final List<Result> realResults = new ArrayList<Result>();

                // Get results of requirement
                for (final String resultString : results) {
                    realResults.add(createResult(resultString,
                            plugin.getPathsConfig().getResultOfRequirement(pathName, reqName, resultString)));
                }

                // Get requirement ID
                final int reqId = plugin.getPathsConfig().getReqId(pathName, reqName);

                // Do sanity check
                if (reqId < 0) {
                    try {
                        throw new Exception("REQ ID COULDN'T BE FOUND! REPORT TO AUTHOR!" + " PATH: " + path
                                + ", REQUIREMENT: " + reqName);
                    } catch (final Exception e) {
                        plugin.getLogger().severe(e.getCause().getMessage());
                        return null;
                    }
                }

                // Get correct name of requirement
                final String correctName = AutorankTools.getCorrectReqName(reqName);

                if (correctName == null) {
                    plugin.getWarningManager()
                            .registerWarning(String.format(
                                    "You are using a '%s' requirement in path '%s', but that requirement doesn't exist!", reqName,
                                    pathName), 10);
                    return null;
                }

                // Create a holder for the path
                final RequirementsHolder reqHolder = new RequirementsHolder(plugin);

                // Option strings seperated
                final List<String[]> optionsList = plugin.getPathsConfig().getRequirementOptions(pathName, reqName);
                
                String customDescription = plugin.getPathsConfig().getReqDescription(pathName, reqName);

                // Find all options of this requirement
                for (final String[] options : optionsList) {
                    final Requirement newRequirement = createRequirement(correctName, options, optional, realResults,
                            plugin.getPathsConfig().useAutoCompletion(pathName, reqName), reqId, reqName, pathName, customDescription);

                    if (newRequirement == null)
                        continue;

                    // Make requirement world-specific if a world was specified
                    // in
                    // the config.
                    if (plugin.getPathsConfig().isRequirementWorldSpecific(pathName, reqName)) {
                        newRequirement.setWorld(plugin.getPathsConfig().getWorldOfRequirement(pathName, reqName));
                    }

                    // Add requirement to holder
                    reqHolder.addRequirement(newRequirement);
                }

                // Now add holder to requirement list of path
                path.addRequirement(reqHolder);
            }

            // Lastly, initialize pre-requisites
            for (String prereqName : plugin.getPathsConfig().getPrerequisites(pathName)) {

                // Implement optional option logic
                final boolean optional = plugin.getPathsConfig().isOptionalPrerequisite(pathName, prereqName);

                // Get requirement ID
                final int prereqId = plugin.getPathsConfig().getPrereqId(pathName, prereqName);

                // Do sanity check
                if (prereqId < 0) {
                    try {
                        throw new Exception("PREREQ ID COULDN'T BE FOUND! REPORT TO AUTHOR!" + " PATH: " + path
                                + ", PREREQUISITES: " + prereqName);
                    } catch (final Exception e) {
                        plugin.getLogger().severe(e.getCause().getMessage());
                        return null;
                    }
                }

                // Get correct name of requirement
                final String correctName = AutorankTools.getCorrectReqName(prereqName);

                if (correctName == null) {
                    plugin.getWarningManager()
                            .registerWarning(String.format(
                                    "You are using a '%s' prerequisite in path '%s', but that prerequisite doesn't exist!", prereqName,
                                    pathName), 10);
                    return null;
                }

                // Create a holder for the path
                final RequirementsHolder prereqHolder = new RequirementsHolder(plugin);

                // Make sure to tell it that this is storing prerequisites
                prereqHolder.setPrerequisite(true);

                // Option strings seperated
                final List<String[]> optionsList = plugin.getPathsConfig().getPrerequisiteOptions(pathName, prereqName);

                // Find all options of this requirement
                for (final String[] options : optionsList) {
                    final Requirement newPrequisite = createPrerequisite(correctName, options, optional, prereqId,
                            prereqName, pathName);

                    if (newPrequisite == null)
                        continue;

                    // Add requirement to holder
                    prereqHolder.addRequirement(newPrequisite);
                }

                // Now add holder to prerequisites list of path
                path.addPrerequisite(prereqHolder);
            }

            // Result for this path (upon choosing)
            final List<String> results = plugin.getPathsConfig().getResultsUponChoosing(pathName);

            // Create a new result List that will get all result when this
            // path is chosen
            final List<Result> realResults = new ArrayList<Result>();

            // Get results of requirement
            for (final String resultString : results) {
                realResults.add(createResult(resultString,
                        plugin.getPathsConfig().getResultValueUponChoosing(pathName, resultString)));
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

    public void setRequirementBuilder(final RequirementBuilder requirementBuilder) {
        this.requirementBuilder = requirementBuilder;
    }

    public void setResultBuilder(final ResultBuilder resultBuilder) {
        this.resultBuilder = resultBuilder;
    }

}
