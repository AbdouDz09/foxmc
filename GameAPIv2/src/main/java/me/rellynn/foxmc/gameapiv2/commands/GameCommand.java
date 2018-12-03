package me.rellynn.foxmc.gameapiv2.commands;

import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 03/06/2017.
 * FoxMC Network.
 */
public abstract class GameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player!");
            return true;
        }
        Player player = (Player) sender;
        Game game = GameAPIv2.get().getGamesManager().whereIs(player);
        if (game == null) {
            player.sendMessage("§cYou must be in a game!");
            return true;
        }
        onCommand(game, player, args);
        return true;
    }

    public abstract void onCommand(Game game, Player player, String[] args);
}
