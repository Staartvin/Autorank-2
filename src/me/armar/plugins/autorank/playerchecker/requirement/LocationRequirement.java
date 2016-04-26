package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;

public class LocationRequirement extends Requirement {

	// x;y;z;world;radius
	// private final List<String> locations = new ArrayList<String>();

	private int radius = -1;
	private String world;
	private int xLocation = 0, yLocation = 0, zLocation = 0;

	@Override
	public String getDescription() {
		return Lang.LOCATION_REQUIREMENT
				.getConfigValue(xLocation + ", " + yLocation + ", " + zLocation + " in " + world);
	}

	@Override
	public String getProgress(final Player player) {
		// Distance between two points:
		// d = sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
		// See for info:
		// http://www.calculatorsoup.com/calculators/geometry-solids/distance-two-points.php

		final Location playerLoc = player.getLocation();

		// Player coords
		int pX, pY, pZ;

		pX = playerLoc.getBlockX();
		pY = playerLoc.getBlockY();
		pZ = playerLoc.getBlockZ();

		String plurOrSing = "meter";

		final int distance = (int) Math
				.sqrt(Math.pow(pX - xLocation, 2) + Math.pow(pY - yLocation, 2) + Math.pow(pZ - zLocation, 2));

		if (distance > 1) {
			plurOrSing = "meters";
		} else {
			plurOrSing = "meter";
		}

		return distance + " " + plurOrSing + " away";
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final Location pLocation = player.getLocation();

		// Positive and negative values
		int xRadiusP, yRadiusP, zRadiusP, xRadiusN, yRadiusN, zRadiusN;

		xRadiusN = xLocation - radius;
		yRadiusN = yLocation - radius;
		zRadiusN = zLocation - radius;

		xRadiusP = xLocation + radius;
		yRadiusP = yLocation + radius;
		zRadiusP = zLocation + radius;

		final World realWorld = Bukkit.getWorld(world);

		if (realWorld == null)
			return false;

		// Player is not in the correct world
		if (!realWorld.getName().equals(pLocation.getWorld().getName()))
			return false;

		// Check if a player is within the radius
		if (pLocation.getBlockX() >= xRadiusN && pLocation.getBlockX() <= xRadiusP) {
			if (pLocation.getBlockY() >= yRadiusN && pLocation.getBlockY() <= yRadiusP) {
				if (pLocation.getBlockZ() >= zRadiusN && pLocation.getBlockZ() <= zRadiusP) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final String[] options) {

		// Location = x;y;z;world;radius
		if (options.length != 5) {
			return false;
		}

		// Save x,y,z
		xLocation = Integer.parseInt(options[0]);
		yLocation = Integer.parseInt(options[1]);
		zLocation = Integer.parseInt(options[2]);

		// Save world
		world = options[3].trim();

		// Save radius
		radius = Integer.parseInt(options[4]);

		if (radius < 0) {
			radius = 0;
		}

		return this.radius != -1;
	}
}
