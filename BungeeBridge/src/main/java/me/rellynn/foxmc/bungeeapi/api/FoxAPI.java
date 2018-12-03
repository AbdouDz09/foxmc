package me.rellynn.foxmc.bungeeapi.api;

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
import me.rellynn.foxmc.api.servers.ServersHandler;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.anticheat.AntiCheatManager;
import me.rellynn.foxmc.bungeeapi.friends.FriendsManager;
import me.rellynn.foxmc.bungeeapi.matches.MatchesManager;
import me.rellynn.foxmc.bungeeapi.moderation.ModerationManager;
import me.rellynn.foxmc.bungeeapi.network.NetworkManager;
import me.rellynn.foxmc.bungeeapi.parties.PartiesManager;
import me.rellynn.foxmc.bungeeapi.players.PlayersManager;
import me.rellynn.foxmc.bungeeapi.servers.ServersManager;
import me.rellynn.foxmc.bungeeapi.utils.uuid.UUIDTranslator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.logging.Logger;

/**
 * Created by gwennaelguich on 07/04/2017.
 * FoxMC Network.
 */
@Getter
public class FoxAPI extends CoreAPI {
    private static FoxAPI foxAPI;

    public static FoxAPI get() {
        return foxAPI;
    }

    public static PlayerData getPlayer(ProxiedPlayer player) {
        return foxAPI.getPlayersHandler().getPlayer(player.getUniqueId());
    }

    // Utils
    private Gson gson = new Gson();
    private UUIDTranslator uuidTranslator;
    // Database
    private String proxyName;
    private SQLManager sqlManager;
    private RedisManager redisManager;
    private PubSubAPI pubSub;
    // Handlers
    private HubsManager hubsManager;
    private NetworkManager networkHandler;
    private ServersHandler serversHandler;
    private MatchesHandler matchesHandler;
    private PlayersHandler playersHandler;
    private ModerationHandler moderationHandler;
    private FriendsHandler friendsHandler;
    private PartiesHandler partiesHandler;
    private AntiCheatManager antiCheatManager;

    public FoxAPI(String proxyName, SQLManager sqlManager, RedisManager redisManager) {
        foxAPI = this;
        this.uuidTranslator = new UUIDTranslator();
        this.proxyName = proxyName;
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
        this.antiCheatManager = new AntiCheatManager();
        FProtocolManager.get().registerChannels("proxies", "proxies." + proxyName);
    }

    @Override
    public void runAsync(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(BridgePlugin.get(), runnable);
    }

    @Override
    public Logger getLogger() {
        return BridgePlugin.get().getLogger();
    }
}
