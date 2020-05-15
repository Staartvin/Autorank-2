package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.McRPGHook;

import java.util.UUID;

public class McRPGSkillLevelRequirement extends AbstractRequirement {

    private McRPGHook hook = null;
    private int skillLevel = -1;
    private String skillName = "all";

    @Override
    public String getDescription() {

        if (skillName.equals("all") || skillName.equals("none")) {
            return Lang.MCRPG_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel, "all skills");
        } else {
            return Lang.MCRPG_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel, skillName);
        }
    }

    @Override
    public String getProgressString(final UUID uuid) {
        int level = 0;

        if (skillName.equalsIgnoreCase("all")) {
            level = hook.getPowerLevel(uuid);
        } else {
            level = hook.getSkillLevel(uuid, skillName);
        }

        return level + "/" + skillLevel;
    }

    @Override
    public boolean meetsRequirement(final UUID uuid) {

        if (skillName.equalsIgnoreCase("all")) {
            return hook.getPowerLevel(uuid) >= skillLevel;
        } else {
            return hook.getSkillLevel(uuid, skillName) >= skillLevel;
        }
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.MCRPG);

        hook = (McRPGHook) this.getDependencyManager().getLibraryHook(Library.MCRPG);

        if (options.length > 0) {
            skillLevel = Integer.parseInt(options[0]);
        }
        if (options.length > 1) {
            skillName = options[1];
        }

        if (skillLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (hook == null || !hook.isHooked()) {
            this.registerWarningMessage("McRPG is not available");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {

        if (skillName.equalsIgnoreCase("all")) {
            return hook.getPowerLevel(uuid) * 1.0d / this.skillLevel;
        } else {
            return hook.getSkillLevel(uuid, skillName) * 1.0d / this.skillLevel;
        }
    }
}
