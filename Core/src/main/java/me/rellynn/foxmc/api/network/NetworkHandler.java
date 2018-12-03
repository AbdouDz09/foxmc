package me.rellynn.foxmc.api.network;

import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.network.packets.BroadcastMessagePacket;
import me.rellynn.foxmc.api.players.packets.PlayerConnectPacket;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.servers.ServerData;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public abstract class NetworkHandler implements FPacketHandler {

    public NetworkHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof BroadcastMessagePacket) {
            handleBroadcast((BroadcastMessagePacket) packet);
        }
    }

    protected void handleBroadcast(BroadcastMessagePacket packet) {}

    public void broadcast(String message) {
        FProtocolManager.get().broadcastProxies(new BroadcastMessagePacket(message));
    }

    public void sendPlayer(UUID uuid, String server) {
        FProtocolManager.get().broadcastProxies(new PlayerConnectPacket(uuid, server));
    }

    public void sendPlayer(UUID uuid, ServerData server) {
        sendPlayer(uuid, server.getName());
    }

    public String getProxy(UUID uuid) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            return jedis.hget("player:" + uuid, "proxy");
        }
    }

    public String getServer(UUID uuid) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            return jedis.hget("player:" + uuid, "server");
        }
    }

    public boolean isOnline(UUID uuid) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            return jedis.exists("player:" + uuid);
        }
    }
}
