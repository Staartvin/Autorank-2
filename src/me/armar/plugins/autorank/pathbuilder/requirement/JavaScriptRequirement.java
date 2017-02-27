package me.armar.plugins.autorank.pathbuilder.requirement;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This requirement checks for javascript evaluations
 * 
 * @author Staartvin
 * 
 */
public class JavaScriptRequirement extends Requirement {

    String code = null;
    // The admin has to provide a description, since this is just an abstract
    // proposition to evaluate.
    private String description = null;

    private ScriptEngine engine = null;

    @Override
    public String getDescription() {
        
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }
        
        return description;
    }

    @Override
    public String getProgress(final Player player) {

        // Unknown progress of Javascript engine.
        return "unknown";
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Put in some shortcuts
        engine.put("Player", player);

        Object result = null;

        // Evaluate expression before storing it.
        try {
            result = engine.eval(code);

            if (!(result instanceof Boolean)) {
                this.getAutorank().getLogger().warning("The expression '" + code + "' is not a valid expression!");
                return false;
            }
        } catch (final ScriptException e) {
            e.printStackTrace();
            return false;
        }

        return (Boolean) result;
    }

    @Override
    public boolean setOptions(final String[] options) {
        if (options.length > 0) {
            code = options[0];
        }

        if (options.length > 1) {
            description = options[1];
        }

        engine = new ScriptEngineManager().getEngineByName("JavaScript");

        engine.put("Server", Bukkit.getServer());

        return code != null && engine != null;
    }
}
