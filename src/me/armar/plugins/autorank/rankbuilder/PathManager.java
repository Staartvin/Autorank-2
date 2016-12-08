package me.armar.plugins.autorank.rankbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.armar.plugins.autorank.Autorank;

/**
 * Handles all things that have to do with paths checking
 * <p>
 * Date created: 16:32:49 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class PathManager {

	private final Autorank plugin;
	private PathBuilder builder;

	// The String is a name of the group, used to get the change groups from that group.
	// The List contains all change groups that this parent group has.
	// One Path class represents one path to take in the group
	private final HashMap<String, List<Path>> changeGroups = new HashMap<String, List<Path>>();

	public PathManager(final Autorank plugin) {
		this.plugin = plugin;
		setBuilder(new PathBuilder(plugin));
	}

	public PathBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(final PathBuilder builder) {
		this.builder = builder;
	}

	public void initialiseFromConfigs() {

		// Clear before starting
		changeGroups.clear();

		if (plugin.getConfigHandler().useAdvancedConfig()) {
			builder.initialisePaths(false, plugin.getAdvancedConfig(), changeGroups);
		} else {
			builder.initialisePaths(true, plugin.getSimpleConfig(), changeGroups);
		}

		for (final String message : debugChangeGroups(true)) {
			plugin.debugMessage(message);
		}

	}

	// Returns a list of strings that can be printed out one for one to get the debug
	public List<String> debugChangeGroups(final boolean deepInfo) {

		final List<String> messages = new ArrayList<String>();

		messages.add(" ------------------- Path debug info ------------------- ");

		for (final Entry<String, List<Path>> entry : changeGroups.entrySet()) {
			final String groupName = entry.getKey();
			final List<Path> groups = entry.getValue();

			messages.add("Group: " + groupName);

			for (final Path group : groups) {
				messages.add("- " + group.getInternalGroup());

				// Provide more info
				if (deepInfo) {
					messages.add("    - " + group.getRequirements().size() + " requirements");
					messages.add("    - " + group.getResults().size() + " results");
				}
			}

			messages.add("----------------------------");

		}

		return messages;
	}

	public List<Path> getChangeGroups(final String groupName) {
		// return empty list if nothing found
		if (!changeGroups.containsKey(groupName))
			return new ArrayList<Path>();

		return changeGroups.get(groupName);
	}

	/**
	 * Get the changegroup that corresponds to the given 'chosenpath' variable.
	 * 
	 * @param parentGroup Group that the Path belongs to
	 * @param chosenPath The internal name of the Path
	 * @return a Path class that corresponds to the internal name given.
	 */
	public Path matchChangeGroup(final String parentGroup, final String chosenPath) {
		final List<Path> changeGroup = this.getChangeGroups(parentGroup);

		if (changeGroup == null)
			return null;

		// If there is only one change group, the player's choice doesn't matter.
		if (changeGroup.size() == 1) {
			return changeGroup.get(0);
		}

		for (final Path change : changeGroup) {
			final String internalName = change.getInternalGroup();

			if (internalName.equals(chosenPath)) {
				return change;
			}
		}
		return null;
	}

	public Path matchChangeGroupFromDisplayName(final String parentGroup, final String displayName) {
		final List<Path> changeGroupList = this.getChangeGroups(parentGroup);

		for (final Path group : changeGroupList) {
			if (group.getDisplayName().toLowerCase().equals(displayName)) {
				return group;
			}
		}

		return null;
	}

	public boolean isDefinedGroup(final String groupName) {
		// Whether this group is a parent group that has a rank up path.

		for (final String group : changeGroups.keySet()) {
			if (group.equalsIgnoreCase(groupName))
				return true;
		}

		return false;
	}

}
