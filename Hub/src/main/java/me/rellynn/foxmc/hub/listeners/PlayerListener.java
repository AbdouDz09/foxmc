package me.rellynn.foxmc.hub.listeners;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubConfig;
import me.rellynn.foxmc.hub.api.HubAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent evt) {
        evt.setSpawnLocation(HubConfig.spawnLocation);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        PlayerData data = FoxAPI.getPlayer(evt.getPlayer());
        evt.setJoinMessage(data.isAtLeast(Rank.FOX_PLUS) ? player.getDisplayName() + " §7§ojoined the lobby!" : null);
        player.setAllowFlight(data.isAtLeast(Rank.VIP) && Settings.hubFly.is(data, SettingValue.ENABLED));
        player.setWalkSpeed(0.3F);
        VirtualMenu.getMenu("hotbar").applyInventory(player);
        if (data.getModLevel() > 0) {
            HubAPI.get().getVisibilityManager().setVisible(player);
        }
        HubConfig.messageOnJoin.forEach(msg -> player.sendMessage(msg.replace("<player>", player.getName())));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        if (evt.getTo().getY() < 0) {
            evt.setTo(HubConfig.spawnLocation);
            player.sendMessage("§7§oDon't go so far!");
            return;
        }
        if (LocationUtils.hasMoved(evt.getFrom(), evt.getTo())) {
            Material under = evt.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
            Location location = player.getLocation();
            if (under == Material.SEA_LANTERN) {
                player.playSound(location, Sound.PISTON_EXTEND, 1.0F, 0.5F);
                (player.isInsideVehicle() ? player.getVehicle() : player).setVelocity(location.getDirection().multiply(8.0D).setY(5.0D));
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        evt.setLeaveMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        evt.setQuitMessage(null);
        PlayerUtils.resetPlayer(evt.getPlayer());
    }
}
