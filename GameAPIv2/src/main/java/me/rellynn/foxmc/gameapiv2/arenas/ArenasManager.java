package me.rellynn.foxmc.gameapiv2.arenas;

import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class ArenasManager {
    private File configFile;
    private FileConfiguration config;
    private Map<String, Arena> knownArenas = new HashMap<>();

    public ArenasManager() {
        this.configFile = new File(GameAPIv2.get().getDataFolder(), "arenas.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadFromDisk();
    }

    private void loadFromDisk() {
        config.getKeys(false).forEach(id -> {
            String mode = config.getString(id + ".mode");
            String map = config.getString(id + ".map");
            int minPlayers = config.getInt(id + ".minplayers", 2);
            int maxPlayers = config.getInt(id + ".maxplayers", Bukkit.getMaxPlayers());
            Arena arena = registerArena(id, mode, map, minPlayers, maxPlayers);
            config.getConfigurationSection(id + ".points").getKeys(true).forEach(key -> {
                String path = id + ".points." + key;
                if (!config.isConfigurationSection(path))
                    arena.setPoint(key, LocationUtils.toLocation(config.getString(path)));
            });
            ConfigurationSection teamsSection = config.getConfigurationSection(id + ".teams");
            teamsSection.getKeys(false).forEach(teamKey -> {
                Team team = arena.registerTeam(teamKey, teamsSection.getString(teamKey + ".prefix"), teamsSection.getString(teamKey + ".name"));
                teamsSection.getConfigurationSection(teamKey + ".points").getKeys(true).forEach(key -> {
                    String path = teamKey + ".points." + key;
                    if (!config.isConfigurationSection(path))
                        team.setPoint(key, LocationUtils.toLocation(teamsSection.getString(path)));
                });
            });
        });
    }

    public void saveToDisk() {
        knownArenas.forEach((id, arena) -> {
            config.set(id + ".mode", arena.getMode());
            config.set(id + ".map", arena.getMap());
            config.set(id + ".minplayers", arena.getMinPlayers());
            config.set(id + ".maxplayers", arena.getMaxPlayers());
            arena.getPoints().forEach((name, location) -> config.set(id + ".points." + name, LocationUtils.toString(location)));
            arena.getTeams().forEach(team -> {
                String teamKey = id + ".teams." + team.getId();
                config.set("prefix", team.getPrefix());
                config.set("name", team.getName());
                team.getPoints().forEach((name, location) -> config.set(teamKey + ".points." + name, LocationUtils.toString(location)));
            });
        });
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace(); // Should never happen?
        }
    }

    public Arena registerArena(String id, String mode, String map, int minPlayers, int maxPlayers) {
        Arena arena = new Arena(id, mode, map, minPlayers, maxPlayers);
        knownArenas.put(id, arena);
        return arena;
    }

    public Arena getArena(String id) {
        return knownArenas.get(id);
    }

    public Arena pickAvailableArena() {
        return knownArenas.values().stream().filter(Arena::isAvailable).findFirst().orElse(null);
    }

    public Set<Arena> getAvailableArenas() {
        return knownArenas.values().stream().filter(Arena::isAvailable).collect(Collectors.toSet());
    }
}
