package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class EffectResult extends Result {

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
