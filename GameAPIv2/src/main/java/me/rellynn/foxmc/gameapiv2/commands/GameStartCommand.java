package me.rellynn.foxmc.gameapiv2.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 03/06/2017.
 * FoxMC Network.
 */
public class GameStartCommand extends GameCommand {

    @Override
    public void onCommand(Game game, Player player, String[] args) {
        if (FoxAPI.getPlayer(player).isAtLeast(Rank.ADMIN)) {
            if (!game.isState(GameState.WAITING)) {
                game.nextPhase();
                player.sendMessage("§cThe game is already started! Going to next phase...");
            } else {
                game.start();
                player.sendMessage("§aGame Started.");
            }
        }
    }
}
