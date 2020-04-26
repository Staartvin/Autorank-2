package me.armar.plugins.autorank.validations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.requirement.InGroupRequirement;
import me.armar.plugins.autorank.warningmanager.WarningManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ValidateHandler {

    private final Autorank plugin;

    public ValidateHandler(final Autorank instance) {
        this.plugin = instance;
    }

    public boolean startValidation() {

        boolean correctSetup = false;

        correctSetup = this.validatePermGroups() && this.validateSettingsConfig();

        return correctSetup;
    }


    /**
     * Check whether all permission groups that are used in the config also exist according to the permission's plugin.
     *
     * @return true if there are no unknown permission groups in the config.
     */
    public boolean validatePermGroups() {

        List<Path> paths = plugin.getPathManager().getAllPaths();

        List<String> permGroups = new ArrayList<>();

        Collection<String> vaultGroups = plugin.getPermPlugHandler().getPermissionPlugin().getGroups();

        for (Path path : paths) {
            List<CompositeRequirement> holders = new ArrayList<>();

            holders.addAll(path.getPrerequisites());
            holders.addAll(path.getRequirements());

            // Check if there are any group requirements/prerequisites
            for (CompositeRequirement reqHolder : holders) {
                for (AbstractRequirement req : reqHolder.getRequirements()) {
                    if (req instanceof InGroupRequirement) {

                        String requirementName = plugin.getPathsConfig().getRequirementName(path.getInternalName(),
                                req.getId(), reqHolder.isPrerequisite());

                        if (requirementName == null || !requirementName.toLowerCase().contains("in group")) {
                            continue;
                        }

                        List<String[]> options = plugin.getPathsConfig().getRequirementOptions(path.getInternalName(),
                                requirementName, reqHolder.isPrerequisite());

                        for (String[] option : options) {
                            if (option.length > 0) {
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
                plugin.getWarningManager().registerWarning("You used the '" + group + "' group, but it was not " +
                        "recognized in your permission plugin!", 10);
                return false;
            }
        }


        return true;
    }

    /**
     * Check the Settings.yml config and see if there are options that are not used anymore.
     *
     * @return true if the settings config is fine, false otherwise.
     */
    public boolean validateSettingsConfig() {

        SettingsConfig config = plugin.getSettingsConfig();

        if (config == null || config.getConfig() == null) return true;

        // Check if the 'use time of' parameter is being used
        if (config.getConfig().get("use time of") != null) {
            plugin.getWarningManager().registerWarning("You are using the 'use time of' setting in the Settings.yml " +
                    "but it doesn't work anymore. Please remove it!");
            return false;
        }

        // Check whether the interval time is valid
        if (config.getIntervalTime() < 1) {
            plugin.getWarningManager().registerWarning("The time between time checks is less than 1, which is " +
                    "illegal!", WarningManager.HIGH_PRIORITY_WARNING);
            return false;
        }

        return true;
    }
}
