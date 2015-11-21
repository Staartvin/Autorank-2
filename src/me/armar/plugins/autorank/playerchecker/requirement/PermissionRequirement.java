package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class PermissionRequirement extends Requirement {

	List<String> permissions = new ArrayList<String>();

	@Override
	public String getDescription() {

		String lang = Lang.PERMISSION_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(permissions, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final String progress = "unknown";
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

		for (final String perm : permissions) {
			if (player.hasPermission(perm))
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			try {
				permissions.add(options[0]);
			} catch (final Exception e) {
				return false;
			}
		}

		return !permissions.isEmpty();
	}
}
