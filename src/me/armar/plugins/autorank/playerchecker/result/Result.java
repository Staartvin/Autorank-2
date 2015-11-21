package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;

public abstract class Result {

	private Autorank autorank;

	public abstract boolean applyResult(Player player);

	public final Autorank getAutorank() {
		return autorank;
	}

	public final void setAutorank(final Autorank autorank) {
		this.autorank = autorank;
	}

	public abstract boolean setOptions(String[] options);

	/*public boolean applyResult(final Player player, final String group) {
		return applyResult(player);
	}*/

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
