package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class FoodEatenRequirement extends Requirement {

	// [0] amount, [1] foodType
	List<FoodWrapper> foodEaten = new ArrayList<FoodWrapper>();

	@Override
	public String getDescription() {

		String desc = "";

		for (int i = 0; i < foodEaten.size(); i++) {
			FoodWrapper wrapper = foodEaten.get(i);
			int amount = wrapper.getAmount();
			String foodType = wrapper.getFoodName();

			if (i == 0) {
				if (foodType == null || foodType.trim().equals("")) {
					desc = Lang.FOOD_EATEN_REQUIREMENT.getConfigValue(amount
							+ " food");
				} else {
					desc = Lang.FOOD_EATEN_REQUIREMENT.getConfigValue(amount
							+ " " + foodType.toLowerCase().replace("_", " ")
							+ "(s)");
				}
			} else {
				if (foodType == null) {
					desc = desc.concat(" or " + amount + " food");
				} else {
					desc = desc.concat(" or " + amount + " "
							+ foodType.toLowerCase().replace("_", " ") + "(s)");
				}
			}
		}

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			desc = desc.concat(" (in world '" + this.getWorld() + "')");
		}

		return desc;
	}

	@Override
	public String getProgress(final Player player) {

		String progress = "";

		for (int i = 0; i < foodEaten.size(); i++) {
			int amount = foodEaten.get(i).getAmount();
			String foodType = foodEaten.get(i).getFoodName();

			final int totalFoodEaten = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.FOOD_EATEN.toString(),
					player.getUniqueId(), this.getWorld(), foodType);

			if (foodType == null) {
				foodType = "food";
			} else {
				foodType = foodType.toLowerCase();
			}

			if (i == 0) {
				progress = progress.concat(totalFoodEaten + "/" + amount + " "
						+ foodType.replace("_", " ") + "(s)");
			} else {
				progress = progress.concat(" or " + totalFoodEaten + "/"
						+ amount + " " + foodType.replace("_", " ") + "(s)");
			}

		}
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!this.getStatsPlugin().isEnabled())
			return false;

		for (int i = 0; i < foodEaten.size(); i++) {
			int amount = foodEaten.get(i).getAmount();
			String foodType = foodEaten.get(i).getFoodName();

			final int totalFoodEaten = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.FOOD_EATEN.toString(),
					player.getUniqueId(), this.getWorld(), foodType);

			if (totalFoodEaten >= amount)
				return true;

		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			final int total = Integer.parseInt(options[0]);
			String foodType = "";

			if (options.length > 1) {
				foodType = options[1].trim();
			}

			foodEaten.add(new FoodWrapper(foodType, total));
		}

		return !foodEaten.isEmpty();
	}
}

class FoodWrapper {

	private ItemStack foodItem;
	private int amount;

	public FoodWrapper(String foodName, int amount) {
		this.setAmount(amount);
		this.setFoodItem(AutorankTools.getFoodItemFromName(foodName));
	}

	public ItemStack getFoodItem() {
		return foodItem;
	}

	public void setFoodItem(ItemStack foodItem) {
		this.foodItem = foodItem;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getFoodName() {
		return AutorankTools.getFoodName(foodItem);
	}
}
