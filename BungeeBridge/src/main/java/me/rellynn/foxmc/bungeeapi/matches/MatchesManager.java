package me.rellynn.foxmc.bungeeapi.matches;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.matches.MatchesHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class MatchesManager extends MatchesHandler {

    @Override
    protected void onMatchCreate(MatchData match) {
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(match.getName(), match.getServer().getAddress(), "", false);
        ProxyServer.getInstance().getServers().put(serverInfo.getName(), serverInfo);
    }

    @Override
    protected void onMatchEnd(MatchData match) {
        ProxyServer.getInstance().getServers().remove(match.getName());
    }
}
