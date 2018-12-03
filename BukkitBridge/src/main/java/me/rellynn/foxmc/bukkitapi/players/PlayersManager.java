package me.rellynn.foxmc.bukkitapi.players;

import me.rellynn.foxmc.api.players.PlayersHandler;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.players.packets.PlayerMessagePacket;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.listeners.NickListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class PlayersManager extends PlayersHandler {

    @Override
    protected void handleMessage(PlayerMessagePacket packet) {
        Player player = Bukkit.getPlayer(packet.getPlayer());
        if (player != null) {
            player.spigot().sendMessage(packet.getMessage());
        }
    }

    @Override
    protected void onSetRank(UUID uuid, Rank rank) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PlayerData data = FoxAPI.getPlayer(player);
            player.setDisplayName(data.getDisplayName());
            BridgePlugin.get().refreshNametag(player, rank);
            if (!rank.isMod() && rank.getLevel() > 0) {
                player.sendMessage("§7Thanks for buying " + rank.getName());
            } else if (rank.isMod()) {
                player.sendMessage("§7You got the rank: " + rank.getName());
            } else {
                player.sendMessage("§cYour rank has been removed!");
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    player.setAllowFlight(rank.getLevel() > 0 && Settings.hubFly.is(data, SettingValue.ENABLED));
                }
            }.runTask(BridgePlugin.get());
        }
    }

    @Override
    protected void onNickChange(UUID uuid, String nickName) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PlayerData data = FoxAPI.getPlayer(player);
            data.set("nickname", !nickName.equals(data.getName()) ? nickName : null);
            BridgePlugin.get().refreshNametag(player, Rank.FOX_PLUS);
            NickListener.updateNickName(player, data);
            // TODO: Refresh game profile...
        }
    }
}
