package me.rellynn.foxmc.hub.votecrates;

import me.rellynn.foxmc.bukkitapi.utils.Hologram;
import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import me.rellynn.foxmc.hub.HubPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class VoteCratesManager extends BukkitRunnable {
    private File configFile;
    private FileConfiguration config;

    private Map<Location, Hologram> crates = new HashMap();

    public VoteCratesManager() {
        HubPlugin plugin = HubPlugin.get();
        configFile = new File(plugin.getDataFolder(), "votecrates.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getCommand("votecrate").setExecutor(new VoteCrateCommand());
        Bukkit.getPluginManager().registerEvents(new VoteCratesListener(this), plugin);
        runTaskTimer(plugin, 20L, 2L);
    }

    public void loadFromDisk() {
        config.getStringList("locations").forEach(str -> setVoteCrate(LocationUtils.toLocation(str)));
    }

    public void saveToDisk() {
        List<String> locationsStr = new ArrayList<>();
        crates.keySet().forEach(location -> locationsStr.add(LocationUtils.toString(location)));
        config.set("locations", locationsStr);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace(); // Should never happen?
        }
    }

    public void showHolograms(Player player) {
        crates.forEach((location, hologram) -> hologram.show(player));
    }

    public boolean isCrate(Block block) {
        return crates.containsKey(block.getLocation());
    }

    public boolean removeCrate(Block block) {
        Hologram hologram = crates.remove(block.getLocation());
        if (hologram != null) {
            hologram.destroy();
            return true;
        }
        return false;
    }

    public void setVoteCrate(Location location) {
        Hologram hologram = new Hologram("§5§lVote §7(Right-Click)", location.clone().add(0.5D, 0.0D, 0.5D));
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) hologram::show);
        crates.put(location, hologram);
    }

    @Override
    public void run() {
        // Play magic particles
        crates.keySet().forEach(location -> location.getWorld().playEffect(location.clone().add(0.5D, 0.5D, 0.5D), Effect.INSTANT_SPELL, null));
    }
}
