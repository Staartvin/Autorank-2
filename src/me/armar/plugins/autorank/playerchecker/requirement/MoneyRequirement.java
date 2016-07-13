package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;

public class MoneyRequirement extends Requirement {

	double minMoney = -1;

	@Override
	public String getDescription() {

		String lang = Lang.MONEY_REQUIREMENT.getConfigValue(minMoney + " " + VaultHandler.economy.currencyNamePlural());

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {

		final double money = VaultHandler.economy.getBalance(player.getPlayer());

		return money + "/" + minMoney + " " + VaultHandler.economy.currencyNamePlural();
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		// UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		if (VaultHandler.economy == null)
			return false;

		return VaultHandler.economy.has(player.getPlayer(), minMoney);
	}

	@Override
	public boolean setOptions(final String[] options) {

		try {
			minMoney = Double.parseDouble(options[0]);
		} catch (final Exception e) {
			return false;
		}

		return minMoney != -1;
	}
}
