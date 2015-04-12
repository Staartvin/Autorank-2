package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class ExpRequirement extends Requirement {

    private final List<Integer> minExps = new ArrayList<Integer>();

    @Override
    public String getDescription() {
        return Lang.EXP_REQUIREMENT.getConfigValue(AutorankTools.seperateList(
                minExps, "or"));
    }

    @Override
    public String getProgress(final Player player) {
        String progress = "";

        final int expLevel = player.getLevel();

        progress = AutorankTools.makeProgressString(minExps, "", expLevel);
        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        final int expLevel = player.getLevel();

        for (final int expMin : minExps) {
            if (expLevel >= expMin) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setOptions(final List<String[]> optionsList) {

        for (final String[] options : optionsList) {
            minExps.add(AutorankTools.stringtoInt(options[0]));
        }

        return !minExps.isEmpty();
    }
}
