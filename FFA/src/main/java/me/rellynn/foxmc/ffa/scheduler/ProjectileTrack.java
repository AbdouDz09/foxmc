package me.rellynn.foxmc.ffa.scheduler;

import me.rellynn.foxmc.ffa.FFAGame;
import me.rellynn.foxmc.ffa.FFAPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 14/06/2017.
 * FoxMC Network.
 */
public class ProjectileTrack extends BukkitRunnable {
    private static FFAGame game = FFAPlugin.getGame();

    private Entity projectile;

    public ProjectileTrack(Entity projectile) {
        this.projectile = projectile;
        runTaskTimer(FFAPlugin.get(), 5, 5);
    }

    @Override
    public void run() {
        if (projectile.isOnGround() || !projectile.isValid()) {
            cancel();
        } else if (game.getSpawn().contains(projectile.getLocation())) {
            projectile.remove();
            cancel();
        }
    }
}
