package me.rellynn.foxmc.ffa.scheduler;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 14/06/2017.
 * FoxMC Network.
 */
@AllArgsConstructor
public class AutoRespawn extends BukkitRunnable {
    private Player player;

    @Override
    public void run() {
        player.spigot().respawn();
    }
}
