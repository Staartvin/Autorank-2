package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class PermissionRequirement extends Requirement {

	private String permission = null;

	@Override
	public String getDescription() {
		return Lang.PERMISSION_REQUIREMENT
				.getConfigValue(new String[] { permission });
	}

	@Override
	public String getProgress(final Player player) {
		final String progress = "unknown";
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		return permission != null && player.hasPermission(permission);
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			permission = options[0];
			return true;
		} catch (final Exception e) {
			permission = null;
			return false;
		}
	}
}
