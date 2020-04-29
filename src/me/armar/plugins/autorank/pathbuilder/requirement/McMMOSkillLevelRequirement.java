package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.McMMOHook;
import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends AbstractRequirement {

    private McMMOHook handler = null;
    private int skillLevel = -1;
    private String skillName = "all";

    @Override
    public String getDescription() {

        if (skillName.equals("all") || skillName.equals("none")) {
            return Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel + "", "all skills");
        } else {
            return Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue(skillLevel + "", skillName);
        }
    }

    @Override
    public String getProgressString(final Player player) {
        int level = 0;

        if (skillName.equalsIgnoreCase("all")) {
            level = handler.getPowerLevel(player);
        } else {
            level = handler.getLevel(player, skillName);
        }

        return level + "/" + skillLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (skillName.equalsIgnoreCase("all")) {
            return handler.getPowerLevel(player) >= skillLevel;
        } else {
            return handler.getLevel(player, skillName) >= skillLevel;
        }
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.MCMMO);

        handler = (McMMOHook) this.getDependencyManager().getLibraryHook(Library.MCMMO);

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

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("mcMMO is not available");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
