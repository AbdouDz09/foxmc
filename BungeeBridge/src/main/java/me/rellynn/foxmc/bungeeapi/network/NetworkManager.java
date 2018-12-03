package me.rellynn.foxmc.bungeeapi.network;

import me.rellynn.foxmc.api.network.NetworkHandler;
import me.rellynn.foxmc.api.network.packets.BroadcastMessagePacket;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class NetworkManager extends NetworkHandler {

    public NetworkManager() {
        PluginManager manager = ProxyServer.getInstance().getPluginManager();
        manager.registerCommand(BridgePlugin.get(), new SetOptionCommand());
        manager.registerCommand(BridgePlugin.get(), new BroadcastCommand());
    }

    @Override
    protected void handleBroadcast(BroadcastMessagePacket packet) {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(packet.getMessage()));
    }

    public void sendPlayer(ProxiedPlayer player, String server) {
        sendPlayer(player.getUniqueId(), server);
    }
}
