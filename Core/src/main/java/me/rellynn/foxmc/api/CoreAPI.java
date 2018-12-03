package me.rellynn.foxmc.api;

import com.google.gson.Gson;
import me.rellynn.foxmc.api.database.RedisManager;
import me.rellynn.foxmc.api.database.SQLManager;
import me.rellynn.foxmc.api.friends.FriendsHandler;
import me.rellynn.foxmc.api.hubs.HubsManager;
import me.rellynn.foxmc.api.matches.MatchesHandler;
import me.rellynn.foxmc.api.moderation.ModerationHandler;
import me.rellynn.foxmc.api.network.NetworkHandler;
import me.rellynn.foxmc.api.parties.PartiesHandler;
import me.rellynn.foxmc.api.players.PlayersHandler;
import me.rellynn.foxmc.api.pubsub.PubSubAPI;
import me.rellynn.foxmc.api.servers.ServersHandler;

import java.util.logging.Logger;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public abstract class CoreAPI {
    private static CoreAPI coreAPI;

    public static CoreAPI get() {
        return coreAPI;
    }

    protected CoreAPI() {
        coreAPI = this;
    }

    /**
     * Run an async task.
     *
     * @param runnable The "task"
     */
    public abstract void runAsync(Runnable runnable);

    public abstract Logger getLogger();

    public abstract Gson getGson();

    public abstract SQLManager getSqlManager();

    public abstract RedisManager getRedisManager();

    public abstract PubSubAPI getPubSub();

    public abstract HubsManager getHubsManager();

    public abstract NetworkHandler getNetworkHandler();

    public abstract ServersHandler getServersHandler();

    public abstract MatchesHandler getMatchesHandler();

    public abstract PlayersHandler getPlayersHandler();

    public abstract ModerationHandler getModerationHandler();

    public abstract FriendsHandler getFriendsHandler();

    public abstract PartiesHandler getPartiesHandler();
}
