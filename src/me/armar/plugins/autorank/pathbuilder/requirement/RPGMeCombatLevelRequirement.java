package me.armar.plugins.autorank.pathbuilder.requirement;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.RPGmeHook;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;

public class RPGMeCombatLevelRequirement extends Requirement {

    private RPGmeHook handler = null;
    private int skillLevel = -1;

    @Override
    public String getDescription() {

        return Lang.RPGME_COMBAT_LEVEL_REQUIREMENT.getConfigValue(skillLevel);
    }

    @Override
    public String getProgress(final Player player) {

        int level = 0;

        level = handler.getCombatLevel(player);

        return level + "/" + skillLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        return handler.getCombatLevel(player) >= skillLevel;
    }

    @Override
    public boolean setOptions(final String[] options) {

        handler = (RPGmeHook) this.getDependencyManager().getLibraryHook(Library.RPGME);

        if (options.length > 0) {
            try {
                skillLevel = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (skillLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("RPGme is not available");
            return false;
        }

        return true;
    }
}
