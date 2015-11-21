package me.armar.plugins.autorank.playerchecker.requirement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class FactionPowerRequirement extends Requirement {

	private final List<Double> factionPowers = new ArrayList<Double>();

	@Override
	public String getDescription() {

		String lang = Lang.FACTIONS_POWER_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(factionPowers, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final DecimalFormat df = new DecimalFormat("#.##");
		final String doubleRounded = df.format(((FactionsHandler) getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS))
				.getFactionPower(player));

		progress = AutorankTools.makeProgressString(factionPowers, "",
				doubleRounded);
		return progress;
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

		for (final double facPower : factionPowers) {
			if (factionPower >= facPower) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			factionPowers.add(Double.parseDouble(options[0]));
		}

		return !factionPowers.isEmpty();
	}
}
