package me.rellynn.foxmc.bukkitapi.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 20/04/2017.
 * FoxMC Network.
 */
public class MenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
            return true;
        }
        Player player = (Player) sender;
        if (FoxAPI.getPlayer(player).isAtLeast(Rank.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You need to specify the menu name.");
                return false;
            }
            VirtualMenu menu = VirtualMenu.getMenu(args[0]);
            if (menu == null) {
                sender.sendMessage(ChatColor.RED + "The menu doesn't exist!");
                return true;
            }
            menu.open(player);
        }
        return true;
    }
}
