package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;
	public static Economy economy = null;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public MoneyRequirement() {
		super();
		setupEconomy();
	}

	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> economyProvider = Bukkit
				.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

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

		return economy != null && economy.has(this.getAutorank().getServer().getOfflinePlayer(player.getUniqueId()), minMoney);
	}

	@Override
	public String getDescription() {
		return Lang.MONEY_REQUIREMENT.getConfigValue(new String[] { minMoney
				+ " " + economy.currencyNamePlural() });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(economy.getBalance(this.getAutorank().getServer().getOfflinePlayer(player.getUniqueId())) + "/"
				+ minMoney);
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
