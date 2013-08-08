package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.playerchecker.result.Result;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;
	public static Economy economy = null;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	public MoneyRequirement() {
		super();
		setupEconomy(); 
	}

	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		try {
			minMoney = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			minMoney = 999999999;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		return economy != null && economy.has(player.getName(), minMoney);
	}

	@Override
	public String getDescription() {
		return LanguageHandler.getLanguage().getMoneyRequirement((int) minMoney);
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
	public String getProgress(Player player) {
		String progress = "";
		progress = progress.concat(economy.getBalance(player.getName()) + "/" + minMoney);
		return progress;
	}
}
