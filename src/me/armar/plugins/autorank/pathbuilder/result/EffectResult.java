package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class EffectResult extends AbstractResult {

    private int data;
    private Effect effect;

    @Override
    public boolean applyResult(final Player player) {
        if (effect != null) {
            player.getWorld().playEffect(player.getLocation(), effect, data);
        }
        return effect != null;
    }

    @Override
    public String getDescription() {
        // Check if we have a custom description. If so, return that instead.
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }

        return Lang.EFFECT_RESULT.getConfigValue(effect.getName());
    }

    @Override
    public boolean setOptions(final String[] options) {
        if (options.length > 0) {
            effect = Effect.valueOf(options[0]);
        }
        if (options.length > 1) {
            data = Integer.parseInt(options[1]);
        }

        return effect != null;
    }

}
