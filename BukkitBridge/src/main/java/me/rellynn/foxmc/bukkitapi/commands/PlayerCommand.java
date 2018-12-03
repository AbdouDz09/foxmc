package me.rellynn.foxmc.bukkitapi.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public abstract class PlayerCommand implements CommandExecutor {
    public Rank rank;

    public PlayerCommand(Rank rank) {
        this.rank = rank;
    }

    public abstract void onCommand(Player player, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player!");
            return true;
        }
        Player player = (Player) sender;
        if (rank != Rank.DEFAULT && !FoxAPI.getPlayer(player).isAtLeast(rank)) {
            player.sendMessage("§cYou can't use this command! You must be " + rank.getName() + " §cor higher.");
        } else {
            onCommand(player, label, args);
        }
        return true;
    }
}
