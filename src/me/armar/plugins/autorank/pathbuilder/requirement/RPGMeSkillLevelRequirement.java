package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.RPGmeHook;
import org.bukkit.entity.Player;

public class RPGMeSkillLevelRequirement extends AbstractRequirement {

    private RPGmeHook handler = null;
    private int skillLevel = -1;
    private String skillName = "all";

    @Override
    public String getDescription() {

        if (skillName.equals("all") || skillName.equals("none")) {
            return Lang.RPGME_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel + "", "all skills");
        } else {
            return Lang.RPGME_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel + "", skillName);
        }
    }

    @Override
    public String getProgressString(final Player player) {
        int level = 0;

        if (skillName.equalsIgnoreCase("all")) {
            level = handler.getTotalLevel(player);
        } else {
            level = handler.getSkillLevel(player, skillName);
        }

        return level + "/" + skillLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isHooked())
            return false;

        if (skillName.equalsIgnoreCase("all")) {
            return handler.getTotalLevel(player) >= skillLevel;
        } else {
            return handler.getSkillLevel(player, skillName) >= skillLevel;
        }
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.RPGME);

        handler = (RPGmeHook) this.getDependencyManager().getLibraryHook(Library.RPGME).orElse(null);

        if (options.length > 0) {
            skillLevel = Integer.parseInt(options[0]);
        }
        if (options.length > 1) {
            skillName = options[1];
        }

        if (skillLevel < 0) {
            this.registerWarningMessage("Skill level was not specified (or smaller than 0)!");
            return false;
        }

        return handler != null;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
