package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class MoneyResult extends Result {

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
     * @see me.armar.plugins.autorank.pathbuilder.result.Result#getDescription()
     */
    @Override
    public String getDescription() {

        String currencyName = "";

        // We need Vault to check economy variables.
        if (this.getAutorank().getDependencyManager().isAvailable(Library.VAULT)) {
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
