package me.rellynn.foxmc.bukkitapi.network;

import me.rellynn.foxmc.api.network.NetworkHandler;
import me.rellynn.foxmc.api.servers.ServerData;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class NetworkManager extends NetworkHandler {

    public void sendPlayer(Player player, String server) {
        sendPlayer(player.getUniqueId(), server);
    }

    public void sendPlayer(Player player, ServerData server) {
        sendPlayer(player.getUniqueId(), server);
    }
}
