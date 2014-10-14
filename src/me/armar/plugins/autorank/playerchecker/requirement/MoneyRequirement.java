package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;

	@Override
	public String getDescription() {
		return Lang.MONEY_REQUIREMENT.getConfigValue(new String[] { minMoney
				+ " " + VaultHandler.economy.currencyNamePlural() });
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getProgress(final Player player) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		String progress = "";
		progress = progress.concat(VaultHandler.economy.getBalance(player
				.getName()) + "/" + minMoney);
		return progress;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean meetsRequirement(final Player player) {

		//UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		return VaultHandler.economy != null
				&& VaultHandler.economy.has(player.getName(), minMoney);
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			minMoney = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			minMoney = 999999999;
			return false;
		}
	}
}
