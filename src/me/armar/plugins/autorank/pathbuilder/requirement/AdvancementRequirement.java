package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdvancementRequirement extends Requirement {

    Advancement advancement = null;
    int advancementCount = -1;
    String advancementName = null;

    @Override
    public String getDescription() {

        String lang;

        if (advancementCount != -1) {
            lang = Lang.ADVANCEMENT_MULTIPLE_REQUIREMENT.getConfigValue(advancementCount);
        } else {
            lang = Lang.ADVANCEMENT_SINGLE_REQUIREMENT.getConfigValue(advancementName);
        }

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        if (advancementCount != -1) {

            int count = getCompletedAdvancements(player).size();

            return count + "/" + advancementCount;
        } else {

            if (!player.getAdvancementProgress(advancement).isDone()) {
                return "advancement not yet obtained.";
            } else {
                return "advancement obtained.";
            }
        }
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        if (advancementCount != -1) {
            return getCompletedAdvancements(player).size() >= advancementCount;
        } else {
            return player.getAdvancementProgress(advancement).isDone();
        }
    }

    public static Advancement getAdvancement(String name) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        // gets all 'registered' advancements on the server.
        while (it.hasNext()) {
            // loops through these.
            Advancement a = it.next();
            if (a.getKey().toString().equalsIgnoreCase(name)) {
                //checks if one of these has the same name as the one you asked for. If so, this is the one it will return.
                return a;
            }
        }
        return null;
    }

    public static List<Advancement> getCompletedAdvancements(Player player) {
        List<Advancement> completedAdvancements = new ArrayList<>();

        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        // gets all 'registered' advancements on the server.
        while (it.hasNext()) {
            // loops through these.
            Advancement a = it.next();

            if (player.getAdvancementProgress(a).isDone()) {
                completedAdvancements.add(a);
            }
        }

        return completedAdvancements;
    }

    public static boolean hasAdvancement(Player player, String name) {
        // name should be something like minecraft:husbandry/break_diamond_hoe
        Advancement a = getAdvancement(name);
        if(a == null){
            // advancement does not exists.
            return false;
        }
        AdvancementProgress progress = player.getAdvancementProgress(a);
        // getting the progress of this advancement.
        return progress.isDone();
        //returns true or false.
    }

    @Override
    public boolean setOptions(final String[] options) {
        String option = options[0].trim();

        if (NumberUtils.isNumber(option)) {
            advancementCount = (int) AutorankTools.stringToDouble(options[0]);

            if (advancementCount < 0) {
                this.registerWarningMessage("No number of advancements provided (or smaller than 0).");
                return false;
            }

        } else {
            advancement = getAdvancement(options[0].trim());

            if (advancement == null) {
                this.registerWarningMessage("No advancement found with that string.");
                return false;
            }

            if (options.length > 1) {
                advancementName = options[1].trim();
            }

            if (advancementName == null) {
                this.registerWarningMessage("No name for the advancement provided.");
                return false;
            }
        }

        return true;
    }
}
