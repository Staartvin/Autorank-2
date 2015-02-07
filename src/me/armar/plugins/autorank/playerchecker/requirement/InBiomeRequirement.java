package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InBiomeRequirement extends Requirement {

	List<String> biomes = new ArrayList<String>();

	@Override
	public String getDescription() {
		String arg = AutorankTools.seperateList(biomes, "or");

		return Lang.IN_BIOME_REQUIREMENT.getConfigValue(arg);
	}

	@Override
	public String getProgress(final Player player) {
		String currentBiome = player.getLocation().getBlock().getBiome()
				.toString();

		String progress = AutorankTools.makeProgressString(biomes, "",
				currentBiome);

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final Location pLocation = player.getLocation();

		for (String biome : biomes) {
			if (pLocation.getBlock().getBiome().toString().equals(biome)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options : optionsList) {

			// biomes
			if (options.length != 1) {
				return false;
			}

			biomes.add(options[0].toUpperCase().replace(" ", "_"));

		}

		return !biomes.isEmpty();
	}
}
