package me.armar.plugins.autorank.validations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.requirement.GroupRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;

public class ValidateHandler {

    private final Autorank plugin;

    public ValidateHandler(final Autorank instance) {
        this.plugin = instance;
    }

    public boolean startValidation() {

        boolean correctSetup = false;

        correctSetup = this.validatePermGroups();

        return correctSetup;
    }

    public boolean validatePermGroups() {

        List<Path> paths = plugin.getPathManager().getPaths();

        List<String> permGroups = new ArrayList<>();
        Collection<String> vaultGroups = plugin.getPermPlugHandler().getPermissionPlugin().getGroups();

        for (Path path : paths) {
            List<RequirementsHolder> holders = new ArrayList<>();

            holders.addAll(path.getPrerequisites());
            holders.addAll(path.getRequirements());

            // Check if there are any group requirements/prerequisites
            for (RequirementsHolder reqHolder : holders) {
                for (Requirement req : reqHolder.getRequirements()) {
                    if (req instanceof GroupRequirement) {

                        String requirementName = plugin.getPathsConfig().getRequirementName(path.getInternalName(),
                                req.getId(), reqHolder.isPrerequisite());

                        if (requirementName == null || !requirementName.toLowerCase().contains("in group")) {
                            continue;
                        }

                        List<String[]> options = plugin.getPathsConfig().getRequirementOptions(path.getInternalName(),
                                requirementName, reqHolder.isPrerequisite());

                        for (String[] option : options) {
                            if (option.length > 0) {
                                System.out.println(requirementName + " - ADDED TO GROUPS: " + option[0]);
                                permGroups.add(option[0]);
                            }
                        }
                    }
                }
            }
        }

        for (String group : permGroups) {
            boolean found = false;

            for (String vaultGroup : vaultGroups) {
                if (group.equalsIgnoreCase(vaultGroup)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                plugin.getWarningManager().registerWarning("You used the '" + group + "' group, but it was not recognized in your permission plugin!", 10);
                return false;
            }
        }


        return true;
    }
}
