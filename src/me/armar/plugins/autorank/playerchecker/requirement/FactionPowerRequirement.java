package me.armar.plugins.autorank.playerchecker.requirement;

import java.text.DecimalFormat;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class FactionPowerRequirement extends Requirement {

	private double factionPower = 0;

	@Override
	public String getDescription() {
		return Lang.FACTIONS_POWER_REQUIREMENT
				.getConfigValue(new String[] { factionPower + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final DecimalFormat df = new DecimalFormat("#.##");
		final String doubleRounded = df.format(((FactionsHandler) getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS))
				.getFactionPower(player));

		progress = progress.concat(doubleRounded + "/" + factionPower);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final FactionsHandler fHandler = (FactionsHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS);

		return fHandler.getFactionPower(player) > factionPower;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			factionPower = Double.parseDouble(options[0]);
			return true;
		} catch (final Exception e) {
			factionPower = 0;
			return false;
		}
	}
}
