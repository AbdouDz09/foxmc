package me.rellynn.foxmc.bukkitapi.moderation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.moderation.ModerationHandler;
import me.rellynn.foxmc.api.moderation.packets.PlayerTeleportPacket;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.events.ModerationTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class ModerationManager extends ModerationHandler implements Listener {
    private Cache<UUID, Player> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public ModerationManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, BridgePlugin.get());
    }

    private void teleport(Player player, Player target) {
        Bukkit.getPluginManager().callEvent(new ModerationTeleportEvent(player, target));
        player.teleport(target);
    }

    @Override
    protected void handleTeleport(PlayerTeleportPacket packet) {
        Player target = Bukkit.getPlayer(packet.getTarget());
        if (target != null) {
            Player player = Bukkit.getPlayer(packet.getPlayer());
            if (player == null) {
                cache.put(packet.getPlayer(), target);
                return;
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    teleport(player, target);
                }
            }.runTask(BridgePlugin.get());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player target = cache.getIfPresent(evt.getPlayer().getUniqueId());
        if (target != null) {
            teleport(evt.getPlayer(), target);
            cache.invalidate(evt.getPlayer().getUniqueId());
        }
    }
}
