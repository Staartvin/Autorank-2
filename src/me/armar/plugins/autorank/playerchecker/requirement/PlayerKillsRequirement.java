package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class PlayerKillsRequirement extends Requirement {

	private int totalPlayersKilled = 0;

	public PlayerKillsRequirement() {
		super();
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			totalPlayersKilled = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			totalPlayersKilled = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return getStatsPlugin().isEnabled()
				&& getStatsPlugin().getNormalStat("players_killed",
						player.getName(), null) >= totalPlayersKilled;
	}

	@Override
	public String getDescription() {
		return Lang.PLAYER_KILLS_REQUIREMENT
				.getConfigValue(new String[] { totalPlayersKilled + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				"players_killed", player.getName(), null)
				+ "/" + totalPlayersKilled);
		return progress;
	}
}
