package me.armar.plugins.autorank.pathbuilder.result;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.language.Lang;
import net.milkbowl.vault.economy.EconomyResponse;

public class MoneyResult extends Result {

    private long money = -1;

    @Override
    public boolean applyResult(final Player player) {
      EconomyResponse res =  VaultHandler.economy.depositPlayer(player, money);
      
      return res.transactionSuccess();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.pathbuilder.result.Result#getDescription()
     */
    @Override
    public String getDescription() {
        return Lang.MONEY_RESULT.getConfigValue(money + " " + VaultHandler.economy.currencyNamePlural());
    }

    @Override
    public boolean setOptions(final String[] options) {

        System.out.println("OPTIONS: " + options);

        if (options.length > 0) {
            System.out.println("OPTIONS 0: " + options[0]);
            money = Long.parseLong(options[0]);
        }
        
        return money >= 0;
    }
}
