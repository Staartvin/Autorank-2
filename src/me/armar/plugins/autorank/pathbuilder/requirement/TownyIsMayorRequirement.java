package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.TownyAdvancedHook;
import org.bukkit.entity.Player;

public class TownyIsMayorRequirement extends AbstractRequirement {

    boolean shouldBeMayor = true;
    private TownyAdvancedHook hook;

    @Override
    public String getDescription() {

        String lang = Lang.TOWNY_IS_MAYOR_REQUIREMENT.getConfigValue();

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(final Player player) {
        return hook.isMayor(player.getName()) + "/" + shouldBeMayor;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!hook.isHooked())
            return false;

        return hook.isMayor(player.getName()) == shouldBeMayor;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.TOWNY_ADVANCED);

        this.hook =
                (TownyAdvancedHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.TOWNY_ADVANCED);

        try {
            shouldBeMayor = Boolean.parseBoolean(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid boolean was provided.");
            return false;
        }

        if (hook == null || !hook.isHooked()) {
            this.registerWarningMessage("Towny is not available");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
