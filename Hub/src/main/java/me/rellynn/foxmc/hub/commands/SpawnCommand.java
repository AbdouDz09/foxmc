package me.rellynn.foxmc.hub.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.hub.HubConfig;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 14/05/2017.
 * FoxMC Network.
 */
public class SpawnCommand extends PlayerCommand {

    public SpawnCommand() {
        super(Rank.DEFAULT);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        player.teleport(HubConfig.spawnLocation);
    }
}
