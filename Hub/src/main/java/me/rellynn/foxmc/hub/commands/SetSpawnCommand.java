package me.rellynn.foxmc.hub.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import me.rellynn.foxmc.hub.HubConfig;
import me.rellynn.foxmc.hub.HubPlugin;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 07/06/2017.
 * FoxMC Network.
 */
public class SetSpawnCommand extends PlayerCommand {
    private final HubPlugin plugin = HubPlugin.get();

    public SetSpawnCommand() {
        super(Rank.OWNER);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        HubConfig.spawnLocation = player.getLocation();
        plugin.getConfig().set("spawnLocation", LocationUtils.toString(HubConfig.spawnLocation));
        plugin.saveConfig();
        player.sendMessage("§aSpawn location set!");
    }
}
