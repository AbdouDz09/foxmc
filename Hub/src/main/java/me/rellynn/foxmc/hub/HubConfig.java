package me.rellynn.foxmc.hub;

import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Created by gwennaelguich on 04/06/2017.
 * FoxMC Network.
 */
public class HubConfig {
    public static List<String> messageOnJoin;
    public static Location spawnLocation;

    static void loadFromDisk() {
        FileConfiguration config = HubPlugin.get().getConfig();
        messageOnJoin = config.getStringList("messageOnJoin");
        spawnLocation = LocationUtils.toLocation(config.getString("spawnLocation", LocationUtils.toString(Bukkit.getWorlds().get(0).getSpawnLocation())));
    }
}
