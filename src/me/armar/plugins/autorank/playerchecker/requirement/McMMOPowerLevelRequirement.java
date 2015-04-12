package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class McMMOPowerLevelRequirement extends Requirement {

    private final List<Integer> powerLevels = new ArrayList<Integer>();
    private McMMOHandler handler = null;

    @Override
    public String getDescription() {
        return Lang.MCMMO_POWER_LEVEL_REQUIREMENT.getConfigValue(AutorankTools
                .seperateList(powerLevels, "or"));
    }

    @Override
    public String getProgress(final Player player) {
        String progress = "";
        final int level = handler.getPowerLevel(player);

        progress = AutorankTools
                .makeProgressString(powerLevels, "", level + "");
        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable()) {
            return false;
        }

        final int level = handler.getPowerLevel(player);

        for (final int realLevel : powerLevels) {
            if (level > 0 && level >= realLevel) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setOptions(final List<String[]> optionsList) {

        handler = (McMMOHandler) this.getDependencyManager().getDependency(
                dependency.MCMMO);

        for (final String[] options : optionsList) {

            if (options.length > 0) {
                powerLevels.add(Integer.parseInt(options[0]));

            }

        }

        return !powerLevels.isEmpty();
    }
}
