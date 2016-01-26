package me.armar.plugins.autorank.playerchecker.requirement;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.language.Lang;

public class FactionPowerRequirement extends Requirement {

	double factionPower = -1;

	@Override
	public String getDescription() {

		String lang = Lang.FACTIONS_POWER_REQUIREMENT
				.getConfigValue(factionPower + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final DecimalFormat df = new DecimalFormat("#.##");
		final String doubleRounded = df.format(((FactionsHandler) getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS))
				.getFactionPower(player));

		return doubleRounded + "/" + factionPower;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		final FactionsHandler fHandler = (FactionsHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS);

		final double factionPower = fHandler.getFactionPower(player);

		return factionPower >= this.factionPower;
	}

	@Override
	public boolean setOptions(String[] options) {
		factionPower = Double.parseDouble(options[0]);

		return factionPower != -1;
	}
}
