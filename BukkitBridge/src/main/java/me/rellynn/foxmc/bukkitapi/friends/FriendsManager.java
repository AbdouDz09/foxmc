package me.rellynn.foxmc.bukkitapi.friends;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.friends.FriendsHandler;
import me.rellynn.foxmc.api.friends.packets.FriendTeleportPacket;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.events.FriendTeleportEvent;
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
public class FriendsManager extends FriendsHandler implements Listener {
    private Cache<UUID, Player> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public FriendsManager() {
        Bukkit.getPluginManager().registerEvents(this, BridgePlugin.get());
    }

    private void teleport(Player player, Player friend) {
        Bukkit.getPluginManager().callEvent(new FriendTeleportEvent(player, friend));
        player.teleport(friend);
    }

    @Override
    public void onFriendTeleport(FriendTeleportPacket packet) {
        Player friend = Bukkit.getPlayer(packet.getFriend());
        if (friend != null) {
            Player player = Bukkit.getPlayer(packet.getPlayer());
            if (player == null) {
                cache.put(packet.getPlayer(), friend);
                return;
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    teleport(player, friend);
                }
            }.runTask(BridgePlugin.get());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player friend = cache.getIfPresent(evt.getPlayer().getUniqueId());
        if (friend != null) {
            teleport(evt.getPlayer(), friend);
            cache.invalidate(evt.getPlayer().getUniqueId());
        }
    }
}
