package me.rellynn.foxmc.api.servers;

import lombok.Getter;
import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.servers.packets.*;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public abstract class ServersHandler implements FPacketHandler {
    protected Map<String, ServerData> servers = new HashMap<>();
    @Getter private boolean storingServers;

    protected ServersHandler(boolean storeServers) {
        this.storingServers = storeServers;
        if (storeServers) {
            loadServers();
            FProtocolManager.get().registerHandler(this);
        }
    }

    public Collection<ServerData> getServers() {
        return servers.values();
    }

    private void loadServers() {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            jedis.keys("server:*").forEach(key -> {
                ServerData data = CoreAPI.get().getGson().fromJson(jedis.get(key), ServerData.class);
                data.getMatches().forEach(match -> match.setServer(data));
                servers.put(data.getName(), data);
                onServerCreate(data);
            });
        }
    }

    protected void update() {
        if (!storingServers) {
            return;
        }
        servers.entrySet().removeIf(entry -> {
            if (!entry.getValue().isUp()) {
                removeServer(entry.getValue());
                return true;
            }
            return false;
        });
    }

    protected void saveToRedis(ServerData data) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getResource()) {
            jedis.set("server:" + data.getName(), CoreAPI.get().getGson().toJson(data));
        }
    }

    protected void removeServer(ServerData data) {
        try (Jedis jedis = CoreAPI.get().getRedisManager().getJedisResource()) {
            jedis.del("server:" + data.getName());
        }
        onServerClose(data);
    }

    public ServerData getData(String name) {
        return servers.get(name);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof ServerCreatePacket) {
            handleCreate((ServerCreatePacket) packet);
        } else if (packet instanceof ServerClosePacket) {
            handleClose((ServerClosePacket) packet);
        } else if (packet instanceof ServerKeepAlivePacket) {
            handleKeepAlive((ServerKeepAlivePacket) packet);
        } else if (packet instanceof ServerChangeStatePacket) {
            handleChangeState((ServerChangeStatePacket) packet);
        } else if (packet instanceof ServerUpdatePlayersPackets) {
            handleUpdatePlayers((ServerUpdatePlayersPackets) packet);
        }
    }

    private void handleCreate(ServerCreatePacket packet) {
        ServerData data = new ServerData(packet.getName(), packet.getHost(), packet.getPort(), packet.getMaxPlayers(), ServerState.JOINABLE);
        servers.put(data.getName(), data);
        onServerCreate(data);
        if (data.getName().startsWith("hub")) {
            CoreAPI.get().getHubsManager().addHubServer(data);
        }
    }

    private void handleClose(ServerClosePacket packet) {
        ServerData data = servers.remove(packet.getServerName());
        if (data != null) {
            onServerClose(data);
            if (data.getName().startsWith("hub")) {
                CoreAPI.get().getHubsManager().removeHubServer(data);
            }
        }
    }

    private void handleKeepAlive(ServerKeepAlivePacket packet) {
        ServerData data = servers.get(packet.getServerName());
        if (data != null) {
            data.heartbeat();
        }
    }

    private void handleChangeState(ServerChangeStatePacket packet) {
        ServerData data = servers.get(packet.getServerName());
        if (data != null) {
            onServerChangeState(data, packet.getNewState());
            data.setState(packet.getNewState());
        }
    }

    private void handleUpdatePlayers(ServerUpdatePlayersPackets packet) {
        ServerData data = servers.get(packet.getServerName());
        if (data != null) {
            data.setPlayers(packet.getPlayers());
        }
    }

    protected void onServerCreate(ServerData data) {}

    protected void onServerClose(ServerData data) {}

    protected void onServerChangeState(ServerData data, ServerState newState) {}
}
