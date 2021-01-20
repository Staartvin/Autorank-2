package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.RPGmeHook;
import org.bukkit.entity.Player;

public class RPGMeCombatLevelRequirement extends AbstractRequirement {

    private RPGmeHook handler = null;
    private int skillLevel = -1;

    @Override
    public String getDescription() {

        return Lang.RPGME_COMBAT_LEVEL_REQUIREMENT.getConfigValue(skillLevel);
    }

    @Override
    public String getProgressString(final Player player) {

        int level = 0;

        level = handler.getCombatLevel(player);

        return level + "/" + skillLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isHooked())
            return false;

        return handler.getCombatLevel(player) >= skillLevel;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.RPGME);

        handler = (RPGmeHook) this.getDependencyManager().getLibraryHook(Library.RPGME).orElse(null);

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

        return handler != null;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
