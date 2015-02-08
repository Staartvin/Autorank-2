package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationRequirement extends Requirement {

	// x;y;z;world;radius
	private final List<String> locations = new ArrayList<String>();

	//private int radius = 1;
	//private String world;
	//private int xLocation = 0, yLocation = 0, zLocation = 0;

	@Override
	public String getDescription() {

		final List<String> stringLocs = new ArrayList<String>();

		for (final String loc : locations) {
			// Save x,y,z
			final int xLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 0));
			final int yLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 1));
			final int zLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 2));

			// Save world
			final String world = AutorankTools.getStringFromSplitString(loc,
					";", 3).trim();

			// Save radius
			//int radius = Integer.parseInt(AutorankTools.getStringFromSplitString(loc, ";", 4));

			stringLocs.add(xLocation + ", " + yLocation + ", " + zLocation
					+ " in " + world);
		}

		return Lang.LOCATION_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(stringLocs, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		// Distance between two points:
		// d = sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
		// See for info: http://www.calculatorsoup.com/calculators/geometry-solids/distance-two-points.php

		final Location playerLoc = player.getLocation();

		// Player coords
		int pX, pY, pZ;

		pX = playerLoc.getBlockX();
		pY = playerLoc.getBlockY();
		pZ = playerLoc.getBlockZ();

		String progress = "";
		String plurOrSing = "meter";

		for (int i = 0; i < locations.size(); i++) {

			final String loc = locations.get(i);

			final int distance = (int) Math.sqrt(Math.pow((pX - Integer
					.parseInt(AutorankTools.getStringFromSplitString(loc, ";",
							0))), 2)
					+ Math.pow((pY - Integer.parseInt(AutorankTools
							.getStringFromSplitString(loc, ";", 1))), 2)
					+ Math.pow((pZ - Integer.parseInt(AutorankTools
							.getStringFromSplitString(loc, ";", 2))), 2));

			if (distance > 1) {
				plurOrSing = "meters";
			} else {
				plurOrSing = "meter";
			}

			if (i == 0) {
				progress = progress.concat(distance + " " + plurOrSing
						+ " away");
			} else {
				progress = progress.concat(" or " + distance + " " + plurOrSing
						+ " away");
			}
		}

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final Location pLocation = player.getLocation();

		for (final String loc : locations) {
			// Positive and negative values
			int xRadiusP, yRadiusP, zRadiusP, xRadiusN, yRadiusN, zRadiusN;

			final int xLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 0));
			final int yLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 1));
			final int zLocation = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 2));

			final int radius = Integer.parseInt(AutorankTools
					.getStringFromSplitString(loc, ";", 4));

			final String world = AutorankTools.getStringFromSplitString(loc,
					";", 3);

			xRadiusN = xLocation - radius;
			yRadiusN = yLocation - radius;
			zRadiusN = zLocation - radius;

			xRadiusP = xLocation + radius;
			yRadiusP = yLocation + radius;
			zRadiusP = zLocation + radius;

			final World realWorld = Bukkit.getWorld(world);

			if (realWorld == null)
				continue;

			// Player is not in the correct world
			if (!realWorld.getName().equals(pLocation.getWorld().getName()))
				continue;

			// Check if a player is within the radius
			if (pLocation.getBlockX() >= xRadiusN
					&& pLocation.getBlockX() <= xRadiusP) {
				if (pLocation.getBlockY() >= yRadiusN
						&& pLocation.getBlockY() <= yRadiusP) {
					if (pLocation.getBlockZ() >= zRadiusN
							&& pLocation.getBlockZ() <= zRadiusP) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			// Location = x;y;z;world;radius
			if (options.length != 5) {
				return false;
			}

			// Save x,y,z
			final int xLocation = Integer.parseInt(options[0]);
			final int yLocation = Integer.parseInt(options[1]);
			final int zLocation = Integer.parseInt(options[2]);

			// Save world
			final String world = options[3].trim();

			// Save radius
			int radius = Integer.parseInt(options[4]);

			if (radius < 0) {
				radius = 0;
			}

			locations.add(xLocation + ";" + yLocation + ";" + zLocation + ";"
					+ world + ";" + radius);

		}

		return !locations.isEmpty();
	}
}
