package me.rellynn.foxmc.bungeeapi.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.moderation.commands.MuteCommand;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class ChatListener implements Listener {
    private Cache<UUID, String> lastMessages = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build();
    private Cache<UUID, Long> antiSpam = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();

    @EventHandler
    public void onChat(ChatEvent evt) {
        if (evt.getSender() instanceof ProxiedPlayer && !evt.isCommand()) {
            ProxiedPlayer player = (ProxiedPlayer) evt.getSender();
            MuteCommand.MuteData muteData = MuteCommand.getMuteData(player);
            if (muteData != null) {
                evt.setCancelled(true);
                player.sendMessage(TextComponent.fromLegacyText("§cYou have been muted §e" + TimeUtils.getDuration(muteData.getUntil() - System.currentTimeMillis()) + " §cfor §r" + muteData.getReason()));
                return;
            }
            // Anti-Spam
            PlayerData data = FoxAPI.getPlayer(player);
            if (antiSpam.getIfPresent(player.getUniqueId()) != null && !data.isAtLeast(Rank.VIP)) {
                evt.setCancelled(true);
                player.sendMessage(TextComponent.fromLegacyText("§cYou can only chat once every 3 seconds! Buy " + Rank.VIP.getName() + " §cto bypass this restriction!"));
                return;
            } else if (data.getModLevel() == 0) {
                if (evt.getMessage().equals(lastMessages.getIfPresent(player.getUniqueId()))) {
                    evt.setCancelled(true);
                    player.sendMessage(TextComponent.fromLegacyText("§cYou can't say the same message twice!"));
                    return;
                }
                lastMessages.put(player.getUniqueId(), evt.getMessage());
            }
            antiSpam.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
