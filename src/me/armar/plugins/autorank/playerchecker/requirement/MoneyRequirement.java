package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class MoneyRequirement extends Requirement {

	private List<Double> minMoney = new ArrayList<Double>();

	@Override
	public String getDescription() {
		return Lang.MONEY_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(minMoney, "or")
				+ " "
				+ VaultHandler.economy.currencyNamePlural());
	}

	@Override
	public String getProgress(final Player player) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		String progress = "";
		
		double money = VaultHandler.economy.getBalance(player.getPlayer());
		
		progress = AutorankTools.makeProgressString(minMoney, VaultHandler.economy.currencyNamePlural(), money + "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		//UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		if (VaultHandler.economy == null) return false;
		
		for (double minMoneys: minMoney) {
			if (VaultHandler.economy.has(player.getPlayer(), minMoneys)) return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {
		
		for (String[] options: optionsList) {
			try {
				minMoney.add(Double.parseDouble(options[0]));
			} catch (final Exception e) {
				return false;
			}	
		}
		
		return !minMoney.isEmpty();
	}
}
