package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

/**
 * This dummy handler is used when no permissions plugin is found. It mocks permissions behavior so that Autorank can
 * still perform checks, but will not succeed.
 */
public class DummyPermissionsHandler extends PermissionsHandler {

    public DummyPermissionsHandler(Autorank plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "Dummy Permissions Handler";
    }

    @Override
    public Collection<String> getPlayerGroups(Player player) {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getWorldGroups(Player player, String world) {
        return Collections.emptyList();
    }

    @Override
    public boolean replaceGroup(Player player, String world, String deletedGroup, String addedGroup) {
        return false;
    }

    @Override
    public boolean setupPermissionsHandler() {
        return true;
    }
}
