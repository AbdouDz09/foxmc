package me.rellynn.foxmc.hub;

import me.rellynn.foxmc.hub.commands.*;
import me.rellynn.foxmc.hub.hotbar.CustomHotbar;
import me.rellynn.foxmc.hub.listeners.BlockListener;
import me.rellynn.foxmc.hub.listeners.PlayerListener;
import me.rellynn.foxmc.hub.listeners.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Created by gwennaelguich on 20/04/2017.
 * FoxMC Network.
 */
abstract class PluginRegistry {
    private static final HubPlugin PLUGIN = HubPlugin.get();

    static void registerEverything() {
        registerEvents();
        registerCommands();
        createMenus();
    }

    private static void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockListener(), PLUGIN);
        pm.registerEvents(new PlayerListener(), PLUGIN);
        pm.registerEvents(new WorldListener(), PLUGIN);
    }

    private static void registerCommands() {
        PLUGIN.getCommand("setspawn").setExecutor(new SetSpawnCommand());
        PLUGIN.getCommand("test").setExecutor(new TestCommand());
        PLUGIN.getCommand("spawn").setExecutor(new SpawnCommand());
        PLUGIN.getCommand("fly").setExecutor(new FlyCommand());
        PLUGIN.getCommand("fw").setExecutor(new FireworkCommand());
    }

    private static void createMenus() {
        new CustomHotbar();
    }
}
