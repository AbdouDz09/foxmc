package me.rellynn.foxmc.bungeeapi.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.BanEntry;
import me.rellynn.foxmc.api.players.database.BanIPEntry;
import me.rellynn.foxmc.api.players.database.MuteEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.moderation.commands.MuteCommand;
import me.rellynn.foxmc.bungeeapi.network.SetOptionCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 03/04/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private BridgePlugin plugin = BridgePlugin.get();
    private FoxAPI foxAPI = FoxAPI.get();

    private Cache<UUID, MuteEntry> muteCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent evt) {
        UUID uuid = evt.getPlayer().getUniqueId();
        PlayerData data = foxAPI.getPlayersHandler().unload(uuid);
        data.setValues(new Object[]{"last_login", System.currentTimeMillis()}, null, null);
        MuteCommand.removeMuteData(uuid);
        foxAPI.runAsync(() -> {
            try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
                jedis.del("player:" + uuid);
            }
        });
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent evt) {
        ProxiedPlayer player = evt.getPlayer();
        UUID uuid = player.getUniqueId();
        // Persist player in redis cache
        foxAPI.runAsync(() -> {
            try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
                foxAPI.getUuidTranslator().persistInfo(player.getName(), uuid, jedis);
                jedis.hset("player:" + uuid, "proxy", foxAPI.getProxyName());
            }
        });
        PlayerData data = foxAPI.getPlayersHandler().validate(uuid); // Store player in local cache
        player.setDisplayName(data.getDisplayName());
        MuteEntry muteEntry = muteCache.getIfPresent(uuid);
        if (muteEntry != null) {
            muteCache.invalidate(uuid);
            MuteCommand.addMuteData(player, muteEntry.getReason(), muteEntry.getDuration());
        }
    }

    @EventHandler
    public void onLogin(LoginEvent evt) {
        evt.registerIntent(plugin);
        foxAPI.getSqlManager().execute(() -> {
            long now = System.currentTimeMillis();
            PendingConnection connection = evt.getConnection();
            UUID uuid = connection.getUniqueId();
            PlayerData data = foxAPI.getPlayersHandler().loadPlayer(uuid);
            if (!SetOptionCommand.isOpened && !data.isAtLeast(Rank.ADMIN)) {
                evt.setCancelled(true);
                evt.setCancelReason(TextComponent.fromLegacyText("§cThe server is currently undergoing maintenance."));
                evt.completeIntent(plugin);
                return;
            }
            if (ProxyServer.getInstance().getPlayers().size() >= SetOptionCommand.maxPlayers && !data.isAtLeast(Rank.VIP)) {
                evt.setCancelled(true);
                evt.setCancelReason(TextComponent.fromLegacyText("§cYou have to be §eVIP §cor higher to join the server!\n             §e§nstore.foxmc.net"));
                evt.completeIntent(plugin);
                return;
            }
            BanIPEntry lastIPBan = BanIPEntry.getLastBan(connection.getAddress().getAddress().getHostAddress());
            if (lastIPBan != null && lastIPBan.isActive()) {
                evt.setCancelled(true);
                evt.setCancelReason(TextComponent.fromLegacyText(
                        "§cYou are temporarily banned for §r" + TimeUtils.getDuration(lastIPBan.getDuration()) +
                                "\n\n§7Reason: §r" + lastIPBan.getReason() +
                                "\n§bTo appeal, please go on our forums.\n§e§nhttps://foxmc.net")
                );
                evt.completeIntent(plugin);
                return;
            }
            BanEntry lastBan = data.getLastBan();
            if (lastBan != null && lastBan.isActive()) {
                evt.setCancelled(true);
                evt.setCancelReason(TextComponent.fromLegacyText(
                        "§cYou are temporarily banned for §r" + TimeUtils.getDuration(lastBan.getDuration()) +
                                "\n\n§7Reason: §r" + lastBan.getReason() +
                                "\n§bTo appeal, please go on our forums.\n§e§nhttps://foxmc.net"
                ));
                evt.completeIntent(plugin);
                return;
            }
            MuteEntry lastMute = data.getLastMute();
            if (lastMute != null && lastMute.isActive()) {
                muteCache.put(uuid, lastMute); // Store the last mute in a temporary cache
            }
            // Update player data
            boolean isInsert = data.get("last_login") == null;
            Object[] updateData = new Object[]{
                    "name", connection.getName(),
                    "last_ip", connection.getAddress().getAddress().getHostAddress(),
                    "last_login", now
            };
            data.set(updateData);
            foxAPI.getSqlManager().execute(() -> {
                // Insert or save
                if ((isInsert && !data.insert())
                        || (!isInsert && !data.save())) {
                    evt.setCancelled(true);
                    evt.setCancelReason(TextComponent.fromLegacyText("§cAn error occurred while updating your data. Please try again later."));
                }
                evt.completeIntent(plugin);
            });
        });
    }
}
