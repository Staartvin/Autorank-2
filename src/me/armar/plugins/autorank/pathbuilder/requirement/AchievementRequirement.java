package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class AchievementRequirement extends Requirement {

    Achievement achievement = null;
    int achievementCount = -1;

    @Override
    public String getDescription() {

        String lang;

        if (achievementCount != -1) {
            lang = Lang.ACHIEVEMENT_MULTIPLE_REQUIREMENT.getConfigValue(achievementCount);
        } else {
            lang = Lang.ACHIEVEMENT_SINGLE_REQUIREMENT.getConfigValue(achievement.toString());
        }

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        if (achievementCount != -1) {

            int count = 0;

            for (Achievement ach : Achievement.values()) {
                if (player.hasAchievement(ach)) {
                    count++;
                }
            }

            return count + "/" + achievementCount;
        } else {
            return "Cannot show progress";
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

        if (achievementCount != -1) {

            int count = 0;

            for (Achievement ach : Achievement.values()) {
                if (player.hasAchievement(ach)) {
                    count++;
                }
            }

            return count >= achievementCount;
        } else {
            return player.hasAchievement(achievement);
        }
    }

    @Override
    public boolean setOptions(final String[] options) {
        try {
            achievementCount = AutorankTools.stringtoInt(options[0]);
            // Check to see if it is a number
        } catch (NumberFormatException e) {
            // It is not a number, so it must be a name
            achievement = Achievement.valueOf(options[0].toUpperCase().replace(" ", "_"));
        }

        return achievementCount != -1 || achievement != null;
    }
}
