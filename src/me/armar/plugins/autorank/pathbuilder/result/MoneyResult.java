package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class MoneyResult extends AbstractResult {

    private long money = -1;

    @Override
    public boolean applyResult(final Player player) {

        // We need Vault to check economy variables.
        if (!this.getAutorank().getDependencyManager().isAvailable(Library.VAULT)) {
            return false;
        }

        EconomyResponse res = VaultHook.getEconomy().depositPlayer(player, money);

        return res.transactionSuccess();
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.pathbuilder.result.AbstractResult#getDescription()
     */
    @Override
    public String getDescription() {
        // Check if we have a custom description. If so, return that instead.
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }

        String currencyName = "";

        // We need Vault to check economy variables.
        if (this.getAutorank().getDependencyManager().isAvailable(Library.VAULT) && VaultHook.getEconomy() != null) {
            currencyName = VaultHook.getEconomy().currencyNamePlural().trim();
        }

        return Lang.MONEY_RESULT.getConfigValue(money + " " + currencyName);
    }

    @Override
    public boolean setOptions(final String[] options) {

        if (options.length > 0) {
            money = Long.parseLong(options[0]);
        }

        return money >= 0;
    }
}
