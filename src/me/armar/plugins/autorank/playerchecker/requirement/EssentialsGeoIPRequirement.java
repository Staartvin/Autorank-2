package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.essentialsapi.EssentialsHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class EssentialsGeoIPRequirement extends Requirement {

	private String location = null;
	private EssentialsHandler essHandler = null;

	@Override
	public String getDescription() {
		return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT
				.getConfigValue(new String[] { location });
	}

	@Override
	public String getProgress(final Player player) {

		final String realLocation = essHandler.getGeoIPLocation(player);

		return realLocation + "/" + location;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final String realLocation = essHandler.getGeoIPLocation(player);

		return (location != null && realLocation != null && location
				.equalsIgnoreCase(realLocation));
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length != 1) {
			return false;
		}

		try {
			location = options[0];

			essHandler = (EssentialsHandler) this.getDependencyManager()
					.getDependency(dependency.ESSENTIALS);

			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
