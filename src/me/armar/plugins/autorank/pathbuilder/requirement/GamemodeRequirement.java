package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class GamemodeRequirement extends AbstractRequirement {

    int gameMode = -1;

    @Override
    public String getDescription() {

        String lang = Lang.GAMEMODE_REQUIREMENT.getConfigValue(gameMode + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(final Player player) {
        return player.getGameMode().getValue() + "/" + gameMode;
    }

    @Override
    protected boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        return player.getGameMode().getValue() == gameMode;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        if (options.length > 0)
            gameMode = (int) AutorankTools.stringToDouble(options[0]);

        if (gameMode < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
