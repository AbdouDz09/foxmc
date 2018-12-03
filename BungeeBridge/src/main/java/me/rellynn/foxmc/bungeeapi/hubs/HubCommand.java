package me.rellynn.foxmc.bungeeapi.hubs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
public class HubCommand extends Command {
    private Cache<UUID, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public HubCommand() {
        super("hub", null, "lobby");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("You must be a player!").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.getServer().getInfo().getName().startsWith("hub")) {
            player.sendMessage(TextComponent.fromLegacyText("§cPlease use Hub Selector to go on another hub."));
            return;
        }
        ServerData server = FoxAPI.get().getHubsManager().getBestHub();
        ServerInfo info = server != null ? ProxyServer.getInstance().getServerInfo(server.getName()) : null;
        if (info == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cNo hub available! Please try again later."));
        } else {
            long time = cache.asMap().getOrDefault(player.getUniqueId(), 0L);
            if (Settings.hubProtection.is(FoxAPI.getPlayer(player), SettingValue.ENABLED) && System.currentTimeMillis() - time >= 5000) {
                player.sendMessage(TextComponent.fromLegacyText("§cAre you sure you want to go to the hub? Please type §e/hub §cagain to confirm."));
                cache.put(player.getUniqueId(), System.currentTimeMillis());
                return;
            }
            if (time > 0) {
                cache.invalidate(player.getUniqueId());
            }
            player.connect(info);
        }
    }
}
