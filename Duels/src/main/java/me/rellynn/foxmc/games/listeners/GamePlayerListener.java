package me.rellynn.foxmc.games.listeners;

import me.rellynn.foxmc.game.api.GameAPI;
import me.rellynn.foxmc.game.api.GameState;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.DuelGameInfo;
import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by gwennaelguich on 29/04/2017.
 * FoxMC Network.
 */
public class GamePlayerListener implements Listener {

    /**
     * Check if the player is in a game and if the player is playing a round.
     *
     * @param player The player
     * @return The result, true = the player is not in a game or the round is started
     */
    private boolean isStarted(Player player) {
        Game game = GameAPI.get().getGame(player);
        if (game != null && !game.isSpectator(player) && game.isState(GameState.STARTED)) {
            DuelGameInfo duelInfo = DuelsPlugin.get().getDuel(game);
            return duelInfo != null && duelInfo.isRoundStarted();
        }
        return true; // Spectators should be able to move
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player && !isStarted((Player) evt.getEntity()))
            evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        boolean hasMoved = evt.getFrom().getX() != evt.getTo().getX() || evt.getFrom().getY() != evt.getTo().getY() || evt.getFrom().getZ() != evt.getTo().getZ();
        if (hasMoved && !isStarted(evt.getPlayer()))
            evt.setTo(evt.getFrom());
    }
}
