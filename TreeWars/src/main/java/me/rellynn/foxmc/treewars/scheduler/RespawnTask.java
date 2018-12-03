package me.rellynn.foxmc.treewars.scheduler;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.treewars.TWPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class RespawnTask extends BukkitRunnable {
    private Player player;
    private int seconds;

    public RespawnTask(Player player, int seconds) {
        this.player = player;
        this.seconds = seconds;
        player.setMetadata("RESPAWNING", new FixedMetadataValue(TWPlugin.get(), true));
        runTaskTimer(TWPlugin.get(), 0L, 20L);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        } else if (seconds <= 0) {
            cancel();
            TWPlugin.getGame().respawn(player);
            player.sendMessage("§eYou have respawned!");
            PacketUtils.sendTitle("§aRESPAWNED!", player);
            PacketUtils.sendSubTitle("", player);
            return;
        }
        String message = "§eRespawning in §c" + seconds + " §esecond" + (seconds != 1 ? "s" : "");
        PacketUtils.sendTitle("§cYOU DIED!", player);
        PacketUtils.sendSubTitle(message, player);
        player.sendMessage(message);
        seconds--;
    }
}
