package me.rellynn.foxmc.bukkitapi.listeners;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.spigotmc.SpigotConfig;

import java.util.regex.Pattern;

/**
 * Created by gwennaelguich on 04/04/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private FoxAPI foxAPI = FoxAPI.get();
    private Pattern blockedCmds = Pattern.compile("(.*):(.*)|pl|plugins|me|ver|version|icanhasbukkit|about|help|\\?", Pattern.CASE_INSENSITIVE);

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent evt) {
        if (evt.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // Pre-load the player on login
            foxAPI.getPlayersHandler().loadPlayer(evt.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        if (evt.getResult() == PlayerLoginEvent.Result.KICK_FULL && FoxAPI.getPlayer(evt.getPlayer()).isAtLeast(Rank.VIP)) {
            evt.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        PlayerData data = foxAPI.getPlayersHandler().validate(player.getUniqueId()); // Store player in local cache
        player.setDisplayName(data.getDisplayName());
        BridgePlugin.get().createNametag(player, data.hasNick() ? Rank.FOX_PLUS : data.getRank());
        PacketUtils.sendPlayerListHeaderFooter("Playing on §e§lFOXMC", "§6Store: §estore.foxmc.net", player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
        evt.setFormat("%1$s§r §8» " + (FoxAPI.getPlayer(evt.getPlayer()).isAtLeast(Rank.VIP) ? "§r" : "§7") + "%2$s");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent evt) {
        String command = evt.getMessage().split(" ")[0].replaceFirst("/", "");
        if (blockedCmds.matcher(command).find() && !FoxAPI.getPlayer(evt.getPlayer()).isAtLeast(Rank.ADMIN)) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage(SpigotConfig.unknownCommandMessage);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        Player player = evt.getPlayer();
        foxAPI.getPlayersHandler().unload(player.getUniqueId());
        BridgePlugin.get().removeNametag(player);
    }
}
