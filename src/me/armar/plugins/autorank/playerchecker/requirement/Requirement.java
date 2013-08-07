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

	public abstract boolean setOptions(String[] options, boolean optional);

	/**
	 * Does it meet the requirements?
	 * @param player Player to check for
	 * @return true if it meets the requirements; false otherwise
	 */
	public abstract boolean meetsRequirement(Player player);

	/**
	 * Gets the description of the requirement
	 * @return string containing description (in locale language)
	 */
	public abstract String getDescription();
	
	/**
	 * Is this an optional requirement?
	 * (Not a main requirement)
	 * @return true when optional; false otherwise.
	 */
	public abstract boolean isOptional();

	public String toString() {
		return this.getClass().getSimpleName();
	}

}
