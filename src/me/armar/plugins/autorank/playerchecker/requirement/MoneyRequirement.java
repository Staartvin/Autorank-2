package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.UUID;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.entity.Player;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;

	@Override
	public String getDescription() {
		return Lang.MONEY_REQUIREMENT.getConfigValue(new String[] { minMoney
				+ " " + VaultHandler.economy.currencyNamePlural() });
	}

	@Override
	public String getProgress(final Player player) {
		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		
		String progress = "";
		progress = progress.concat(VaultHandler.economy.getBalance(this
				.getAutorank().getServer()
				.getOfflinePlayer(uuid))
				+ "/" + minMoney);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		
		return VaultHandler.economy != null
				&& VaultHandler.economy.has(this.getAutorank().getServer()
						.getOfflinePlayer(uuid), minMoney);
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
