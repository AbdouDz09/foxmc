package me.rellynn.foxmc.bungeeapi.players;

import me.rellynn.foxmc.api.players.PlayersHandler;
import me.rellynn.foxmc.api.players.packets.PlayerConnectPacket;
import me.rellynn.foxmc.api.players.packets.PlayerMessagePacket;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class PlayersManager extends PlayersHandler {

    public PlayersManager() {
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new MessageCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new ReplyCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new NickCommand());
    }

    @Override
    protected void handleConnect(PlayerConnectPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(packet.getServerName());
            if (info == null) {
                player.disconnect(TextComponent.fromLegacyText("§cUnable to find the server!"));
                return;
            }
            player.connect(info);
        }
    }

    @Override
    protected void handleMessage(PlayerMessagePacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            player.sendMessage(packet.getMessage());
        }
    }

    @Override
    protected void onVote(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(TextComponent.fromLegacyText("§aThanks for voting! §eYou have won §675 FoxCoins."));
        }
    }
}

