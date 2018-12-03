package me.rellynn.foxmc.bukkitapi.servers;

import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServersHandler;
import me.rellynn.foxmc.api.servers.packets.ServerClosePacket;
import me.rellynn.foxmc.api.servers.packets.ServerCreatePacket;
import me.rellynn.foxmc.api.servers.packets.ServerKeepAlivePacket;
import me.rellynn.foxmc.api.servers.packets.ServerUpdatePlayersPackets;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class ServersManager extends ServersHandler implements Listener {
    private FoxAPI foxAPI = FoxAPI.get();
    private ServerData data = foxAPI.getServerData();

    public ServersManager() {
        super(FoxAPI.get().getServerData().getName().startsWith("hub"));
        Bukkit.getPluginManager().registerEvents(this, BridgePlugin.get());
        Bukkit.getScheduler().runTaskTimerAsynchronously(BridgePlugin.get(), this::update, 600L, 600L);
        FProtocolManager.get().broadcast(new ServerCreatePacket(data));
        saveToRedis(data);
    }

    public void save() {
        saveToRedis(data);
    }

    public void stop() {
        FProtocolManager.get().broadcast(new ServerClosePacket(data));
        removeServer(data);
    }

    public void updatePlayers(int incrBy) {
        data.addPlayers(incrBy);
        FProtocolManager.get().broadcast(new ServerUpdatePlayersPackets(data));
        foxAPI.runAsync(this::save);
    }

    protected void update() {
        data.heartbeat();
        FProtocolManager.get().broadcast(new ServerKeepAlivePacket(data));
        save();
        super.update();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        updatePlayers(1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        updatePlayers(-1);
    }
}
