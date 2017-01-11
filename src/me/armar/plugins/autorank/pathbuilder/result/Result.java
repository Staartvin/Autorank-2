package me.armar.plugins.autorank.pathbuilder.result;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;

public abstract class Result {

	private Autorank autorank;

	public abstract boolean applyResult(Player player);

	public final Autorank getAutorank() {
		return autorank;
	}

	public abstract String getDescription();

	public final void setAutorank(final Autorank autorank) {
		this.autorank = autorank;
	}

	/*public boolean applyResult(final Player player, final String group) {
		return applyResult(player);
	}*/

	public abstract boolean setOptions(String[] options);

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
