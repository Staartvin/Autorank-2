package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.LanguageHandler;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyRequirement extends Requirement {

	private double minMoney = 999999999;
	public static Economy economy = null;
	private boolean optional = false;

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
	public boolean setOptions(String[] options, boolean optional) {
		this.optional = optional;
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

}
