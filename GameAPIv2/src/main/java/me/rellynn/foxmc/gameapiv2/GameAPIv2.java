package me.rellynn.foxmc.gameapiv2;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.arenas.ArenasManager;
import me.rellynn.foxmc.gameapiv2.commands.ArenaCommand;
import me.rellynn.foxmc.gameapiv2.commands.GameStartCommand;
import me.rellynn.foxmc.gameapiv2.commands.LeaveCommand;
import me.rellynn.foxmc.gameapiv2.games.GamesManager;
import me.rellynn.foxmc.gameapiv2.listeners.GameListener;
import me.rellynn.foxmc.gameapiv2.listeners.NetworkListener;
import me.rellynn.foxmc.gameapiv2.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
@Getter
public class GameAPIv2 extends JavaPlugin {
    private static GameAPIv2 instance;

    public static GameAPIv2 get() {
        return instance;
    }

    private GamesManager gamesManager;
    private ArenasManager arenasManager;

    @Override
    public void onEnable() {
        instance = this;
        gamesManager = new GamesManager();
        arenasManager = new ArenasManager();
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NetworkListener(), this);
        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("gamestart").setExecutor(new GameStartCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
    }

    @Override
    public void onDisable() {
        gamesManager.unregisterAll();
        arenasManager.saveToDisk();
    }
}
