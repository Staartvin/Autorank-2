package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.util.AutorankTools;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FoodEatenRequirement extends AbstractRequirement {

    FoodWrapper foodEaten = null;

    @Override
    public String getDescription() {

        String desc = "";

        final int amount = foodEaten.getAmount();
        final String foodType = foodEaten.getFoodName();

        if (foodType == null || foodType.trim().equals("")) {
            desc = Lang.FOOD_EATEN_REQUIREMENT.getConfigValue(amount + " food");
        } else {
            desc = Lang.FOOD_EATEN_REQUIREMENT
                    .getConfigValue(amount + " " + foodType.toLowerCase().replace("_", " ") + "(s)");
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

        final int amount = foodEaten.getAmount();
        String foodType = foodEaten.getFoodName();

        final int totalFoodEaten = getStatsPlugin().getNormalStat(StatsPlugin.StatType.FOOD_EATEN,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "foodType", foodType));

        if (foodType == null) {
            foodType = "food";
        } else {
            foodType = foodType.toLowerCase();
        }

        progress = progress.concat(totalFoodEaten + "/" + amount + " " + foodType.replace("_", " ") + "(s)");

        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!this.getStatsPlugin().isEnabled())
            return false;

        final int amount = foodEaten.getAmount();
        final String foodType = foodEaten.getFoodName();

        final int totalFoodEaten = getStatsPlugin().getNormalStat(StatsPlugin.StatType.FOOD_EATEN,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "foodType", foodType));

        return totalFoodEaten >= amount;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        final int total = Integer.parseInt(options[0]);
        String foodType = "";

        if (options.length > 1) {
            foodType = options[1].trim();
        }

        Material foodMaterial = Material.matchMaterial(foodType);

        if (foodMaterial == null) {
            this.registerWarningMessage("Food '" + foodType + "' is not a valid type of food.");
            return false;
        }

        foodEaten = new FoodWrapper(foodMaterial, total);

        return true;
    }
}

class FoodWrapper {

    private int amount;
    private ItemStack foodItem;

    public FoodWrapper(final Material foodMaterial, final int amount) {
        this.setAmount(amount);
        this.setFoodItem(new ItemStack(foodMaterial, amount));
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getFoodItem() {
        return foodItem;
    }

    public String getFoodName() {
        return foodItem.getType().name();
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public void setFoodItem(final ItemStack foodItem) {
        this.foodItem = foodItem;
    }
}
