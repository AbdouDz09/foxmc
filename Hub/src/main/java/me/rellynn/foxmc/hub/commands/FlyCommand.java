package me.rellynn.foxmc.hub.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 26/06/2017.
 * FoxMC Network.
 */
public class FlyCommand extends PlayerCommand {

    public FlyCommand() {
        super(Rank.VIP);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        boolean newState = Settings.hubFly.toggle(FoxAPI.getPlayer(player));
        player.setAllowFlight(newState);
        player.sendMessage(newState ? "§aFlight mode enabled." : "§cFlight mode disabled.");
    }
}
