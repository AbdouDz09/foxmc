package me.rellynn.foxmc.hub.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 24/04/2017.
 * FoxMC Network.
 */
public class TestCommand extends PlayerCommand {

    public TestCommand() {
        super(Rank.ADMIN);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        player.sendMessage("§aTest!");
    }
}
