package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.entity.Player;

public abstract class Requirement {

	private Autorank autorank;

	public final void setAutorank(Autorank autorank) {
		this.autorank = autorank;
	}

	public final Autorank getAutorank() {
		return autorank;
	}

	public abstract boolean setOptions(String[] options);

	public abstract boolean meetsRequirement(Player player);

	public abstract String getDescription();

	public String toString() {
		return this.getClass().getSimpleName();
	}

}
