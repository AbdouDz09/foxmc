package me.rellynn.foxmc.bukkitapi.api;

import com.google.gson.Gson;
import lombok.Getter;
import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.database.RedisManager;
import me.rellynn.foxmc.api.database.SQLManager;
import me.rellynn.foxmc.api.friends.FriendsHandler;
import me.rellynn.foxmc.api.hubs.HubsManager;
import me.rellynn.foxmc.api.matches.MatchesHandler;
import me.rellynn.foxmc.api.moderation.ModerationHandler;
import me.rellynn.foxmc.api.parties.PartiesHandler;
import me.rellynn.foxmc.api.players.PlayersHandler;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.pubsub.PubSubAPI;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.friends.FriendsManager;
import me.rellynn.foxmc.bukkitapi.matches.MatchesManager;
import me.rellynn.foxmc.bukkitapi.moderation.ModerationManager;
import me.rellynn.foxmc.bukkitapi.network.NetworkManager;
import me.rellynn.foxmc.bukkitapi.parties.PartiesManager;
import me.rellynn.foxmc.bukkitapi.players.PlayersManager;
import me.rellynn.foxmc.bukkitapi.servers.ServersManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Created by gwennaelguich on 06/04/2017.
 * FoxMC Network.
 */
@Getter
public class FoxAPI extends CoreAPI {
    private static FoxAPI foxAPI;

    public static FoxAPI get() {
        return foxAPI;
    }

    public static PlayerData getPlayer(Player player) {
        return foxAPI.getPlayersHandler().getPlayer(player.getUniqueId());
    }

    // Utils
    private Gson gson = new Gson();
    // Database
    private ServerData serverData;
    private SQLManager sqlManager;
    private RedisManager redisManager;
    private PubSubAPI pubSub;
    // Handlers
    private HubsManager hubsManager;
    private NetworkManager networkHandler;
    private ServersManager serversHandler;
    private MatchesHandler matchesHandler;
    private PlayersHandler playersHandler;
    private ModerationHandler moderationHandler;
    private FriendsHandler friendsHandler;
    private PartiesHandler partiesHandler;

    public FoxAPI(ServerData data, SQLManager sqlManager, RedisManager redisManager) {
        foxAPI = this;
        this.serverData = data;
        this.sqlManager = sqlManager;
        this.redisManager = redisManager;
        this.pubSub = new PubSubAPI();
        this.hubsManager = new HubsManager();
        this.networkHandler = new NetworkManager();
        this.serversHandler = new ServersManager();
        this.matchesHandler = new MatchesManager();
        this.playersHandler = new PlayersManager();
        this.moderationHandler = new ModerationManager();
        this.friendsHandler = new FriendsManager();
        this.partiesHandler = new PartiesManager();
        FProtocolManager.get().registerChannels("servers", "servers." + data.getName());
    }

    @Override
    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(BridgePlugin.get(), runnable);
    }

    @Override
    public Logger getLogger() {
        return BridgePlugin.get().getLogger();
    }
}
