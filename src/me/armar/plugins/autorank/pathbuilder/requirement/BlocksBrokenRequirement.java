package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class BlocksBrokenRequirement extends Requirement {

    BlocksWrapper wrapper = null;

    @Override
    public String getDescription() {
        final ItemStack item = wrapper.getItem();

        final StringBuilder arg = new StringBuilder(item.getAmount() + " ");

        if (wrapper.getDisplayName() != null) {
            // Show displayname instead of material name
            arg.append(wrapper.getDisplayName());
        } else {
            if (item.getType().toString().contains("AIR")) {
                arg.append("blocks");
            } else {
                arg.append(item.getType().toString().replace("_", " ").toLowerCase());
            }

            if (wrapper.showShortValue()) {
                arg.append(" (Dam. value: " + item.getDurability() + ")");
            }
        }

        String lang = Lang.BROKEN_BLOCKS_REQUIREMENT.getConfigValue(arg.toString());

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getProgress(final Player player) {

        int progressBar = 0;

        if (wrapper.getItem().getTypeId() <= 0 && !wrapper.showShortValue()) {
            progressBar = getStatsPlugin().getNormalStat(StatsPlugin.StatType.TOTAL_BLOCKS_BROKEN,
                    player.getUniqueId(), AutorankTools.makeStatsInfo());
        } else {
            if (wrapper.showShortValue()) {
                // Use datavalue
                progressBar = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN, player.getUniqueId(),
                        AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(),
                                "dataValue", wrapper.getItem().getDurability()));
            } else {
                if (wrapper.getItem().getType() == Material.AIR) {
                    // Id was not given so only check amount
                    progressBar = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN,
                            player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));
                } else {
                    // ID was given, but no data value
                    progressBar = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN,
                            player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID",
                                    wrapper.getItem().getTypeId()));
                }
            }
        }

        return progressBar + "/" + wrapper.getBlocksBroken();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean meetsRequirement(final Player player) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int blockID = wrapper.getItem().getTypeId();
        final int blocksBroken = wrapper.getBlocksBroken();

        int progress = 0;

        if (blockID <= 0 && !wrapper.showShortValue()) {
            progress = getStatsPlugin().getNormalStat(StatsPlugin.StatType.TOTAL_BLOCKS_BROKEN, player.getUniqueId(),
                    AutorankTools.makeStatsInfo("world", this.getWorld()));
        } else {
            if (wrapper.showShortValue()) {
                // Use datavalue
                progress = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN, player.getUniqueId(),
                        AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(),
                                "dataValue", wrapper.getItem().getDurability()));
            } else {
                if (wrapper.getItem().getType() == Material.AIR) {
                    // Id was not given so only check amount
                    progress = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN,
                            player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));
                } else {
                    // ID was given, but no data value
                    progress = getStatsPlugin().getNormalStat(StatsPlugin.StatType.BLOCKS_BROKEN,
                            player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID",
                                    wrapper.getItem().getTypeId()));
                }
            }
        }

        return progress >= blocksBroken;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        int id = -1;
        int amount = 1;
        short data = 0;

        String displayName = null;
        boolean showShortValue = false;
        boolean useDisplayName = false;

        if (options.length > 0) {
            amount = Integer.parseInt(options[0].trim());
        }
        if (options.length > 1) {
            id = (int) AutorankTools.stringToDouble(options[0]);
            amount = Integer.parseInt(options[1].trim());
        }
        if (options.length > 2) {
            data = (short) AutorankTools.stringToDouble(options[2]);
            // Short value can make a difference, thus we show it.
            showShortValue = true;
        }
        if (options.length > 3) {
            // Displayname
            displayName = options[3];
        }
        if (options.length > 4) {
            // use display name?
            useDisplayName = (options[4].equalsIgnoreCase("true") ? true : false);
        }

        ItemStack item = new ItemStack(id, amount, data);

        wrapper = new BlocksWrapper(item, displayName, showShortValue, useDisplayName);

        wrapper.setBlocksBroken(amount);

        if (amount < 0) {
            this.registerWarningMessage("Amount is not provided or smaller than 0.");
            return false;
        }

        if (wrapper == null) {
            this.registerWarningMessage("No valid block provided.");
            return false;
        }

        return true;
    }
}

class BlocksWrapper extends ItemWrapper {

    private int blocksBroken; // How many items does the player need to break?

    public BlocksWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
        super(item, displayName, showShortValue, useDisplayName);
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(final int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}
