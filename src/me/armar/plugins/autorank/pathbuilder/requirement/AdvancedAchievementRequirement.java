package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.AdvancedAchievementsHandler;

public class AdvancedAchievementRequirement extends Requirement {

    String achievementName;
    int achievementCount = -1;

    private AdvancedAchievementsHandler handler;

    @Override
    public String getDescription() {

        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }

        String lang;

        if (achievementCount > 0) {
            lang = Lang.ADVANCED_ACHIEVEMENTS_MULTIPLE_REQUIREMENT.getConfigValue(achievementCount);
        } else {
            lang = Lang.ADVANCED_ACHIEVEMENTS_SINGLE_REQUIREMENT.getConfigValue(achievementName);
        }

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        if (achievementCount > 0) {

            int count = handler.getNumberOfAchievements(player);

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

        if (achievementCount > 0) {

            int count = handler.getNumberOfAchievements(player);

            return count >= achievementCount;
        } else {
            return handler.hasAchievement(player, achievementName);
        }
    }

    @Override
    public boolean setOptions(final String[] options) {

        handler = (AdvancedAchievementsHandler) this.getDependencyManager()
                .getDependencyHandler(Dependency.ADVANCEDACHIEVEMENTS);

        System.out.println("OPTIONS: " + options[0]);
        
        try {
            achievementCount = AutorankTools.stringtoInt(options[0]);
            // Check to see if it is a number
        } catch (NumberFormatException e) {
            // It is not a number, so it must be a name
            achievementName = options[0].trim();
        }

        return achievementCount != -1 || achievementName != null;
    }
}
