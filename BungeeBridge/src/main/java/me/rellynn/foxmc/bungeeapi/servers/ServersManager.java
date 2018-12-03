package me.rellynn.foxmc.bungeeapi.servers;

import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServersHandler;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class ServersManager extends ServersHandler {
    private FoxAPI foxAPI = FoxAPI.get();

    public ServersManager() {
        super(true);
        ProxyServer.getInstance().getScheduler().schedule(BridgePlugin.get(), this::update, 30L, 30L, TimeUnit.SECONDS);
    }

    @Override
    protected void onServerCreate(ServerData data) {
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(data.getName(), data.getAddress(), "", false);
        ProxyServer.getInstance().getServers().put(data.getName(), serverInfo);
        foxAPI.getLogger().info("Created server (" + data.getName() + ") port=" + data.getAddress().getPort());
    }

    @Override
    protected void onServerClose(ServerData data) {
        ProxyServer.getInstance().getServers().remove(data.getName());
        foxAPI.getLogger().info("Server removed (" + data.getName() + ")");
    }
}

