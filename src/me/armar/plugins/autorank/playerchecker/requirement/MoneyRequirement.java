package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;

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

	@Override
	public boolean meetsRequirement(final Player player) {

		return VaultHandler.economy != null
				&& VaultHandler.economy.has(this.getAutorank().getServer()
						.getOfflinePlayer(player.getUniqueId()), minMoney);
	}

	@Override
	public String getDescription() {
		return Lang.MONEY_REQUIREMENT.getConfigValue(new String[] { minMoney
				+ " " + VaultHandler.economy.currencyNamePlural() });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(VaultHandler.economy.getBalance(this
				.getAutorank().getServer()
				.getOfflinePlayer(player.getUniqueId()))
				+ "/" + minMoney);
		return progress;
	}
}
