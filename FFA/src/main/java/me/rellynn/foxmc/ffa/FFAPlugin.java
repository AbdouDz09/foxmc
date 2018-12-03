package me.rellynn.foxmc.ffa;

import lombok.Getter;
import me.rellynn.foxmc.ffa.commands.SaveCommand;
import me.rellynn.foxmc.ffa.listeners.BlockListener;
import me.rellynn.foxmc.ffa.listeners.PlayerListener;
import me.rellynn.foxmc.ffa.listeners.WorldListener;
import me.rellynn.foxmc.ffa.pubsub.KillsManager;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class FFAPlugin extends JavaPlugin {
    private static FFAPlugin instance;

    public static FFAPlugin get() {
        return instance;
    }

    public static FFAGame getGame() {
        return instance.game;
    }

    private FFAGame game;
    @Getter private KillsManager killsManager;

    @Override
    public void onEnable() {
        instance = this;
        Arena arena = GameAPIv2.get().getArenasManager().pickAvailableArena();
        if (arena == null) {
            getLogger().severe("No arena available! Disabling the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        game = new FFAGame(arena);
        killsManager = new KillsManager();
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getCommand("save").setExecutor(new SaveCommand());
    }
}
