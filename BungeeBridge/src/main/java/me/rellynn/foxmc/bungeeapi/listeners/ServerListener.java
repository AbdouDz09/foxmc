package me.rellynn.foxmc.bungeeapi.listeners;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.network.SetOptionCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

/**
 * Created by gwennaelguich on 18/04/2017.
 * FoxMC Network.
 */
public class ServerListener implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent evt) {
        evt.getResponse().setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', SetOptionCommand.motd)));
        evt.getResponse().getPlayers().setOnline(FoxAPI.get().getServersHandler().getServers().stream().map(ServerData::getPlayers).reduce(0, Integer::sum));
        evt.getResponse().getPlayers().setMax(SetOptionCommand.maxPlayers);
    }

    @EventHandler
    public void onServerKick(ServerKickEvent evt) {
        evt.setCancelled(true);
        if (evt.getState() == ServerKickEvent.State.CONNECTING && evt.getPlayer().getServer() != null) {
            evt.setCancelServer(evt.getPlayer().getServer().getInfo());
            evt.getPlayer().sendMessage(evt.getKickReasonComponent());
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent evt) {
        if (evt.getTarget().getName().equals("failover")) {
            ServerData bestHub = FoxAPI.get().getHubsManager().getBestHub();
            ServerInfo serverInfo;
            if (bestHub == null || (serverInfo = ProxyServer.getInstance().getServerInfo(bestHub.getName())) == null) {
                evt.setCancelled(true);
                evt.getPlayer().disconnect(TextComponent.fromLegacyText("§cNo hub available! Buy " + Rank.VIP.getName() + " §cto join the server even when it's full!"));
                return;
            }
            evt.setTarget(serverInfo);
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent evt) {
        FoxAPI.get().runAsync(() -> {
            try (Jedis jedis = FoxAPI.get().getRedisManager().getResource()) {
                jedis.hset("player:" + evt.getPlayer().getUniqueId(), "server", evt.getServer().getInfo().getName());
            }
        });
    }
}
