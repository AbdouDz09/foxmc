package me.rellynn.foxmc.ffa.scheduler;

import me.rellynn.foxmc.ffa.FFAPlugin;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 13/06/2017.
 * FoxMC Network.
 */
public class AuraEffect extends BukkitRunnable {
    private static final int PARTICLES = 20;
    private static final double INCREMENT = (2 * Math.PI) / PARTICLES;
    private static final double RADIUS = 0.5;

    private Player player;
    private Effect effect;
    private long ticks = 200;

    public AuraEffect(Player player, Effect effect) {
        this.player = player;
        this.effect = effect;
        runTaskTimer(FFAPlugin.get(), 0, 10);
    }

    public void reset() {
        ticks = 200;
    }

    @Override
    public void run() {
        if (ticks <= 0 || !player.isValid()) {
            cancel();
            player.removeMetadata("AURA_EFFECT", FFAPlugin.get());
            return;
        }
        Location location = player.getLocation().add(0, 2, 0);
        for (int i = 0; i < PARTICLES; i++) {
            double angle = i * INCREMENT;
            double x = location.getX() + RADIUS * Math.cos(angle);
            double z = location.getZ() + RADIUS * Math.sin(angle);
            player.getWorld().spigot().playEffect(new Location(player.getWorld(), x, location.getY(), z), effect, 0, 0, 0, 0, 0, 0, 1, 16);
        }
        ticks -= 10;
    }
}
