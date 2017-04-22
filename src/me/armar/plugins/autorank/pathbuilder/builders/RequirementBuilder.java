package me.armar.plugins.autorank.pathbuilder.builders;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;    

public class RequirementBuilder {

    private static final Map<String, Class<? extends Requirement>> reqs = new HashMap<String, Class<? extends Requirement>>();

    public Requirement create(final String type) {
        Requirement res = null;
        final Class<? extends Requirement> c = reqs.get(type);
        if (c != null)
            try {
                res = c.newInstance();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        else {
            Bukkit.getServer().getConsoleSender().sendMessage(
                    "[Autorank] " + ChatColor.RED + "Requirement '" + type + "' is not a valid requirement type!");
            return null;
        }
        return res;
    }

    public static void registerRequirement(final String type, final Class<? extends Requirement> requirement) {
        // Add type to the list
        reqs.put(type, requirement);

        // Add type to the list of AutorankTools so it can use the correct name.
        AutorankTools.registerRequirement(type);
    }

}
