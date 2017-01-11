package me.armar.plugins.autorank.pathbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * Handles all things that have to do with paths checking
 * <p>
 * Date created: 16:32:49 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class PathManager {

	private PathBuilder builder;
	// A list of paths any player is able to take
	private List<Path> paths = new ArrayList<Path>();

	private final Autorank plugin;

	public PathManager(final Autorank plugin) {
		this.plugin = plugin;
		setBuilder(new PathBuilder(plugin));
	}

	/**
	 * Return a list of messages that represents debug information of the paths.
	 * 
	 * @return a list of strings.
	 */
	public List<String> debugPaths() {

		final List<String> messages = new ArrayList<String>();

		messages.add(" ------------------- Path debug info ------------------- ");

		for (Path path : paths) {
			String pathName = path.getDisplayName();
			List<RequirementsHolder> requirements = path.getRequirements();
			List<RequirementsHolder> prerequisites = path.getPrerequisites();
			List<Result> results = path.getResults();

			int count = 1;

			messages.add("Path: " + pathName);

			messages.add("Prerequisites: ");

			for (RequirementsHolder prereq : prerequisites) {
				messages.add("    " + count + ". " + prereq.getDescription());
				count++;
			}

			// Reset count again
			count = 1;

			messages.add("Requirements: ");

			for (RequirementsHolder req : requirements) {
				messages.add("    " + count + ". " + req.getDescription());
				count++;
			}

			// Reset count again
			count = 1;

			messages.add("Results: ");

			for (Result res : results) {
				messages.add("    " + count + ". " + res.getDescription());
				count++;
			}

			messages.add("----------------------------");

		}

		return messages;
	}

	public PathBuilder getBuilder() {
		return builder;
	}

	/**
	 * Get the path that the player is currently on.
	 * 
	 * @param uuid UUID of the player
	 * @return path of the player, or null if not found.
	 */
	public Path getCurrentPath(UUID uuid) {
		String chosenPath = plugin.getPlayerDataConfig().getChosenPath(uuid);

		// Unknown path, so return null
		if (chosenPath.equalsIgnoreCase("unknown")) {
			return null;
		}

		return this.matchPath(chosenPath, true);
	}

	/**
	 * Get a list of all paths that are defined in the paths.yml file.
	 * 
	 * @return a list of {@link Path} objects.
	 */
	public List<Path> getPaths() {
		return paths;
	}

	/**
	 * Get possible paths a player can take. A player can choose a path if he
	 * meets the prerequisites.
	 * 
	 * @param player Player
	 * @return List of paths that a player can choose.
	 */
	public List<Path> getPossiblePaths(Player player) {
		List<Path> possibilities = new ArrayList<>();

		for (Path path : this.getPaths()) {
			// Add path to possibilities, if player meets all prerequisites
			if (path.meetsPrerequisites(player)) {
				possibilities.add(path);
			}
		}

		return possibilities;
	}

	/**
	 * Initialise paths from paths.yml file.
	 */
	public void initialiseFromConfigs() {

		// Clear before starting
		paths.clear();

		paths = builder.initialisePaths();

		for (final String message : debugPaths()) {
			plugin.debugMessage(message);
		}

	}

	/**
	 * 
	 * Get the path that corresponds to the given chosenPath string.
	 * 
	 * @param chosenPath The display name of the path
	 * @param isCaseSensitive true if we only match paths that have the exact
	 *            wording, taking into account case sensitivity.
	 * @return matching path or null if none found.
	 */
	public Path matchPath(String chosenPath, boolean isCaseSensitive) {
		for (final Path path : this.getPaths()) {

			if (isCaseSensitive) {
				if (path.getDisplayName().equals(chosenPath)) {
					return path;
				}
			} else {
				if (path.getDisplayName().equalsIgnoreCase(chosenPath)) {
					return path;
				}
			}
		}

		return null;
	}

	public void setBuilder(final PathBuilder builder) {
		this.builder = builder;
	}

}
