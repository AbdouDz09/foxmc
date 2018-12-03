package me.rellynn.foxmc.hub;

import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class HubPlugin extends JavaPlugin {
    private static HubPlugin instance;

    public static HubPlugin get() {
        return instance;
    }

    private HubAPI hubAPI;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        HubConfig.loadFromDisk();
        hubAPI = new HubAPI();
        hubAPI.getNpcManager().loadFromDisk();
        hubAPI.getVoteCratesManager().loadFromDisk();
        MountsManager.registerEntities();
        PluginRegistry.registerEverything();
    }

    @Override
    public void onDisable() {
        hubAPI.getPetsManager().removePets();
        hubAPI.getGadgetsManager().cancelGadgets();
        hubAPI.getNpcManager().saveToDisk();
        hubAPI.getVoteCratesManager().saveToDisk();
    }
}
