package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

public class MessageResult extends AbstractResult {

    String msg = null;

    @Override
    public boolean applyResult(final Player player) {
        if (player == null) {
            return false;
        }

        msg = msg.replace("&p", player.getName());
        msg = msg.replaceAll("(&([a-z0-9]))", "\u00A7$2");

        player.sendMessage(msg);
        return msg != null;
    }

    @Override
    public String getDescription() {
        return Lang.MESSAGE_RESULT.getConfigValue(msg);
    }

    @Override
    public boolean setOptions(final String[] options) {
        if (options.length > 0)
            msg = options[0];
        return msg != null;
    }

}
