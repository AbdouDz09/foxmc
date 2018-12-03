package me.rellynn.foxmc.bungeeapi.scheduler;

import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.network.SetOptionCommand;
import redis.clients.jedis.Jedis;

/**
 * Created by gwennaelguich on 21/04/2017.
 * FoxMC Network.
 */
public class RefreshProxyOptions implements Runnable {
    private FoxAPI foxAPI = FoxAPI.get();

    @Override
    public void run() {
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            SetOptionCommand.motd = jedis.get("option:motd");
            SetOptionCommand.maxPlayers = Integer.parseInt(jedis.get("option:maxplayers"));
            SetOptionCommand.isOpened = "opened".equals(jedis.get("option:status"));
        }
    }
}
