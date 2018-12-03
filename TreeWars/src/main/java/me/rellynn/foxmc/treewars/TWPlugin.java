package me.rellynn.foxmc.treewars;

import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.treewars.game.TWGame;
import me.rellynn.foxmc.treewars.listeners.BlockListener;
import me.rellynn.foxmc.treewars.listeners.EntityListener;
import me.rellynn.foxmc.treewars.listeners.PlayerListener;
import me.rellynn.foxmc.treewars.listeners.WorldListener;
import me.rellynn.foxmc.treewars.shops.ItemsShop;
import me.rellynn.foxmc.treewars.shops.UpgradesShop;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class TWPlugin extends JavaPlugin {
    private static TWPlugin instance;

    public static TWPlugin get() {
        return instance;
    }

    public static TWGame getGame() {
        return instance.game;
    }

    private TWGame game;

    @Override
    public void onEnable() {
        instance = this;
        Arena arena = GameAPIv2.get().getArenasManager().pickAvailableArena();
        if (arena == null) {
            getLogger().severe("No arena available! Disabling the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        game = new TWGame(arena);
        game.setupArena();
        register(new BlockListener());
        register(new EntityListener());
        register(new PlayerListener());
        register(new WorldListener());
        new ItemsShop();
        new UpgradesShop();
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
