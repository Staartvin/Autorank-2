package me.armar.plugins.autorank.playerchecker.additionalrequirement;

import org.bukkit.entity.Player;

public class MoneyRequirement extends AdditionalRequirement {

	private double minMoney = 999999999;

	public MoneyRequirement() {
		super();
		// TODO VAULT
	}

	@Override
	public boolean setOptions(String[] options) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		return "Need a minimum amount of money of " + minMoney + ".";
	}

}
