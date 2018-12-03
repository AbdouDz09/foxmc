package me.rellynn.foxmc.bukkitapi;

import me.rellynn.foxmc.api.database.RedisManager;
import me.rellynn.foxmc.api.database.SQLManager;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServerState;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.commands.LagCommand;
import me.rellynn.foxmc.bukkitapi.commands.MenuCommand;
import me.rellynn.foxmc.bukkitapi.listeners.NickListener;
import me.rellynn.foxmc.bukkitapi.listeners.PlayerListener;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.VirtualTeam;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class BridgePlugin extends JavaPlugin {
    private static BridgePlugin plugin;

    public static BridgePlugin get() {
        return plugin;
    }

    private Map<Player, VirtualTeam> teams = new WeakHashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        String serverName = getConfig().getString("serverName", "");
        if (serverName.isEmpty()) {
            getLogger().severe("Unable to start the server without a server name.");
            getServer().shutdown();
            return;
        }
        ServerData data = new ServerData(serverName, Bukkit.getIp(), Bukkit.getPort(), Bukkit.getMaxPlayers(), ServerState.JOINABLE);
        SQLManager sqlManager = new SQLManager(getConfig().getString("database.url"), getConfig().getString("database.user"), getConfig().getString("database.password"), 3);
        RedisManager redisManager = new RedisManager(getConfig().getString("redis.host"));
        FoxAPI foxAPI = new FoxAPI(data, sqlManager, redisManager);
        foxAPI.getServersHandler().updatePlayers(Bukkit.getOnlinePlayers().size());
        getServer().getOnlinePlayers().forEach((Consumer<Player>) player -> {
            foxAPI.getPlayersHandler().loadPlayer(player.getUniqueId());
            foxAPI.getPlayersHandler().validate(player.getUniqueId());
        });
        // Register listeners and commands
        getCommand("lag").setExecutor(new LagCommand());
        getCommand("menu").setExecutor(new MenuCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NickListener(), this);
    }

    @Override
    public void onDisable() {
        FoxAPI.get().getServersHandler().stop();
        FoxAPI.get().getRedisManager().killConnection();
    }

    /*
    Nametags
     */
    public void createNametag(Player player, Rank rank) {
        teams.forEach((p, team) -> PacketUtils.sendPacket(player, team.create()));
        VirtualTeam team = new VirtualTeam(player.getName());
        team.setPrefix(rank.getPrefix());
        PacketPlayOutScoreboardTeam teamPacket = team.create(player.getName());
        getServer().getOnlinePlayers().forEach((Consumer<Player>) online -> PacketUtils.sendPacket(online, teamPacket));
        teams.put(player, team);
    }

    public void removeNametag(Player player) {
        VirtualTeam team = teams.remove(player);
        if (team != null) {
            PacketPlayOutScoreboardTeam teamPacket = team.destroy();
            getServer().getOnlinePlayers().forEach((Consumer<Player>) online -> PacketUtils.sendPacket(online, teamPacket));
        }
    }

    public void refreshNametag(Player player, Rank rank) {
        VirtualTeam team = teams.get(player);
        team.setPrefix(rank.getPrefix());
        PacketPlayOutScoreboardTeam packet = team.update();
        getServer().getOnlinePlayers().forEach((Consumer<Player>) online -> PacketUtils.sendPacket(online, packet));
    }
}
