package me.armar.plugins.autorank.rankbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.armar.plugins.autorank.Autorank;

/**
 * Handles all things that have to do with change groups checking
 * <p>
 * Date created: 16:32:49 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class ChangeGroupManager {

	private final Autorank plugin;
	private ChangeGroupBuilder builder;

	// The String is a name of the group, used to get the change groups from that group.
	// The List contains all change groups that this parent group has.
	// One ChangeGroup class represents one path to take in the group
	private final HashMap<String, List<ChangeGroup>> changeGroups = new HashMap<String, List<ChangeGroup>>();

	public ChangeGroupManager(final Autorank plugin) {
		this.plugin = plugin;
		setBuilder(new ChangeGroupBuilder(plugin));
	}

	public ChangeGroupBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(final ChangeGroupBuilder builder) {
		this.builder = builder;
	}

	public void initialiseFromConfigs() {

		// Clear before starting
		changeGroups.clear();

		if (plugin.getConfigHandler().useAdvancedConfig()) {
			builder.initialiseChangeGroups(false, plugin.getAdvancedConfig(),
					changeGroups);
		} else {
			builder.initialiseChangeGroups(true, plugin.getSimpleConfig(),
					changeGroups);
		}

		for (final String message : debugChangeGroups(true)) {
			plugin.debugMessage(message);
		}

	}

	// Returns a list of strings that can be printed out one for one to get the debug
	public List<String> debugChangeGroups(final boolean deepInfo) {

		final List<String> messages = new ArrayList<String>();

		messages.add(" ------------------- ChangeGroup debug info ------------------- ");

		for (final Entry<String, List<ChangeGroup>> entry : changeGroups
				.entrySet()) {
			final String groupName = entry.getKey();
			final List<ChangeGroup> groups = entry.getValue();

			messages.add("Group: " + groupName);

			for (final ChangeGroup group : groups) {
				messages.add("- " + group.getInternalGroup());

				// Provide more info
				if (deepInfo) {
					messages.add("    - " + group.getRequirementsHolders().size()
							+ " requirements");
					messages.add("    - " + group.getResults().size()
							+ " results");
				}
			}

			messages.add("----------------------------");

		}

		return messages;
	}

	public List<ChangeGroup> getChangeGroups(final String groupName) {
		// return empty list if nothing found
		if (!changeGroups.containsKey(groupName))
			return new ArrayList<ChangeGroup>();

		return changeGroups.get(groupName);
	}

	/**
	 * Get the changegroup that corresponds to the given 'chosenpath' variable.
	 * 
	 * @param parentGroup Group that the ChangeGroup belongs to
	 * @param chosenPath The internal name of the ChangeGroup
	 * @return a ChangeGroup class that corresponds to the internal name given.
	 */
	public ChangeGroup matchChangeGroup(final String parentGroup,
			final String chosenPath) {
		final List<ChangeGroup> changeGroup = this.getChangeGroups(parentGroup);

		if (changeGroup == null)
			return null;

		// If there is only one change group, the player's choice doesn't matter.
		if (changeGroup.size() == 1) {
			return changeGroup.get(0);
		}

		for (final ChangeGroup change : changeGroup) {
			final String internalName = change.getInternalGroup();

			if (internalName.equals(chosenPath)) {
				return change;
			}
		}
		return null;
	}

	public ChangeGroup matchChangeGroupFromDisplayName(
			final String parentGroup, final String displayName) {
		final List<ChangeGroup> changeGroupList = this
				.getChangeGroups(parentGroup);

		for (final ChangeGroup group : changeGroupList) {
			if (group.getDisplayName().toLowerCase().equals(displayName)) {
				return group;
			}
		}

		return null;
	}

}
