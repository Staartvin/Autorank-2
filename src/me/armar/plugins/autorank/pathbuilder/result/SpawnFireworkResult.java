package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;


public class SpawnFireworkResult extends AbstractResult {

    private Color colour = Color.ORANGE;
    private Location location;
    private int power = 1;
    private String target = "player";
    private Type type = Type.BALL;

    @Override
    public boolean applyResult(final Player player) {
        if (player == null)
            return false;

        final Location loc = (target.equals("player")) ? player.getLocation() : player.getWorld().getSpawnLocation();

        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();
        final FireworkEffect effect = FireworkEffect.builder().withColor(colour).with(type).build();

        fwm.addEffect(effect);
        fwm.setPower(power);

        fw.setFireworkMeta(fwm);

        player.teleport(location);
        return location != null;
    }

    @Override
    public String getDescription() {
        String targetLocation = (target.equals("player") ? "your location" : "at spawn");

        return Lang.SPAWN_FIREWORK_RESULT.getConfigValue(targetLocation);
    }

    @Override
    public boolean setOptions(final String[] options) {

        if (options.length < 6)
            return false;

        target = options[0];
        power = Integer.parseInt(options[1]);
        type = Type.valueOf(options[2].toUpperCase().replace(" ", "_"));
        // Colour is RGB code
        colour = Color.fromRGB(Integer.parseInt(options[3]), Integer.parseInt(options[4]),
                Integer.parseInt(options[5]));

        return target != null;
    }

}
