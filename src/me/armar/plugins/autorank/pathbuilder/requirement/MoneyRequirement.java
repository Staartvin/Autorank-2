package me.armar.plugins.autorank.pathbuilder.requirement;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.VaultHook;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.vaultapi.PluginLibraryHandler;
import me.armar.plugins.autorank.language.Lang;

public class MoneyRequirement extends Requirement {

    double minMoney = -1;

    @Override
    public String getDescription() {

        String currencyName = "";

        if (this.getAutorank().getDependencyManager().isAvailable(Library.VAULT)) {
            currencyName  = VaultHook.getEconomy().currencyNamePlural().trim();
        }

        String lang = Lang.MONEY_REQUIREMENT.getConfigValue(minMoney + " " + currencyName);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        double money = 0;
        String currencyName = "";

        if (this.getAutorank().getDependencyManager().isAvailable(Library.VAULT)) {
            money = VaultHook.getEconomy().getBalance(player.getPlayer());
            currencyName = VaultHook.getEconomy().currencyNamePlural().trim();
        }

        return money + "/" + minMoney + " " + currencyName;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        // If Vault is not available or economy is not set up.
        if (!this.getAutorank().getDependencyManager().isAvailable(Library.VAULT) || VaultHook.getEconomy() == null)
            return false;

        return VaultHook.getEconomy().has(player.getPlayer(), minMoney);
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
