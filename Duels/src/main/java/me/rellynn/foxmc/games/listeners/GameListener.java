package me.rellynn.foxmc.games.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.game.api.BroadcastType;
import me.rellynn.foxmc.game.api.GameState;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.game.api.game.events.game.GameEndEvent;
import me.rellynn.foxmc.game.api.game.events.game.GameStartEvent;
import me.rellynn.foxmc.game.api.game.events.player.GamePlayerDeathEvent;
import me.rellynn.foxmc.game.api.game.events.player.GamePlayerLeaveEvent;
import me.rellynn.foxmc.game.api.game.events.player.GamePlayerRespawnEvent;
import me.rellynn.foxmc.games.DuelGameInfo;
import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by gwennaelguich on 25/04/2017.
 * FoxMC Network.
 */
public class GameListener implements Listener {
    private DuelsPlugin plugin = DuelsPlugin.get();

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        Game game = evt.getGame();
        DuelGameInfo duelInfo = new DuelGameInfo(game);
        plugin.createDuel(game, duelInfo);
        game.getPlayers().forEach(player -> player.teleport(game.getFreeSpawn()));
        game.broadcast(BroadcastType.TITLE, "");
        game.broadcast(BroadcastType.SUBTITLE, duelInfo.getFirstPlayer().getDisplayName() + " §cvs §r" + duelInfo.getSecondPlayer().getDisplayName());
        duelInfo.startRound();
    }

    @EventHandler
    public void onGamePlayerDeath(GamePlayerDeathEvent evt) {
        Game game = evt.getGame();
        Player player = evt.getPlayer();
        DuelGameInfo duelInfo = plugin.getDuel(game);
        if (duelInfo != null) {
            Player opponent = duelInfo.getOpponent(player);
            // Player win detection
            duelInfo.increaseWin(opponent);
            duelInfo.stopRoundTimer();
            if (duelInfo.hasWon(opponent)) {
                evt.getGame().end(opponent);
            }
            // Messages
            String opponentHealth = String.format("%.1f", opponent.getHealth() / 2d);
            player.sendMessage("§7Your opponent has §c" + opponentHealth + " ❤ §7left!");
            opponent.sendMessage("§7You have won the round with §c" + opponentHealth + " ❤");
            game.increaseStatistic(player, "deaths");
            if (evt.getKiller() == opponent) {
                game.increaseStatistic(opponent, "kills");
                // Killing sound
                String currentSound = FoxAPI.getPlayer(opponent).getCurrentItem("duels.sounds");
                if (currentSound != null) {
                    Sound sound = Sound.valueOf(currentSound);
                    player.playSound(player.getEyeLocation(), sound, 1, 1);
                    opponent.playSound(opponent.getEyeLocation(), sound, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onGamePlayerRespawn(GamePlayerRespawnEvent evt) {
        if (evt.getGame().isState(GameState.STARTED)) {
            DuelGameInfo duelInfo = plugin.getDuel(evt.getGame());
            duelInfo.getOpponent(evt.getPlayer()).teleport(evt.getGame().getFreeSpawn());
            duelInfo.startRound();
            duelInfo.regenBlocks();
        }
    }

    @EventHandler
    public void onGamePlayerLeave(GamePlayerLeaveEvent evt) {
        Game game = evt.getGame();
        if (!game.isState(GameState.END) && game.getPlayers().size() < game.getArena().getMinPlayers()) {
            game.broadcast("§cYour opponent left the game. You will be returned to lobby.");
            game.end(null);
        }
    }

    /**
     * Retrieve the ELO rating of a player.
     *
     * @param player The player
     * @return The player rating
     */
    private int getRating(Player player) {
        JsonElement pointsElem = FoxAPI.getPlayer(player).getCustomData("duels.points");
        if (pointsElem == null) {
            return 100;
        }
        return pointsElem.getAsInt();
    }

    /**
     * Calculate the new ELO rating of a player.
     *
     * @param elo1  The player elo
     * @param elo2  The opponent elo
     * @param score 0 = loose, 1 = win
     * @return The new rating
     */
    private int calculateRating(int elo1, int elo2, int score) {
        double exponent = (double) (elo2 - elo1) / 400;
        double expectedOutcome = (1 / (1 + (Math.pow(10, exponent))));
        int kFactor = elo1 < 500 ? 32 : (elo1 < 750 ? 24 : 16);
        int newRating = (int) Math.round(elo1 + kFactor * (score - expectedOutcome));
        return Math.max(0, newRating);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent evt) {
        Game game = evt.getGame();
        DuelGameInfo duelInfo = plugin.removeDuel(game);
        if (duelInfo != null) {
            duelInfo.regenBlocks(); // Regen arena
            Player winner = evt.getWinner();
            if (winner != null) {
                Player looser = duelInfo.getOpponent(winner);
                // New Ratings & Stats
                int winnerElo = getRating(winner), looserElo = getRating(looser);
                int winnerRating = calculateRating(winnerElo, looserElo, 1), looserRating = calculateRating(looserElo, winnerElo, 0);
                FoxAPI.getPlayer(winner).setCustomData("duels.points", new JsonPrimitive(winnerRating));
                FoxAPI.getPlayer(looser).setCustomData("duels.points", new JsonPrimitive(looserRating));
                game.increaseStatistic(looser, "losses");
                game.increaseStatistic(winner, "wins");
                // Messages, Melodies & FoxCoins
                int winnerRounds = duelInfo.getRoundsWon(winner), looserRounds = duelInfo.getRoundsWon(looser);
                looser.sendMessage("§7You have lost the fight versus §r" + winner.getDisplayName() + " §c§l" + looserRounds + "§8-§a§l" + winnerRounds);
                winner.sendMessage("§7You have won the fight versus §r" + looser.getDisplayName() + " §a§l" + winnerRounds + "§8-§c§l" + looserRounds);
                game.broadcast("§a" + winner.getName() + " (" + winnerRating + " points) (+" + (winnerRating - winnerElo) + ") §8| §c" + looser.getName() + " (" + looserRating + " points) (-" + (looserElo - looserRating) + ")");
                game.broadcast(winner.getDisplayName() + " §6won the game! §a§k|§b§k|§c§k|§d§k|§e§k|§b§l Congratulations §a§k|§b§k|§c§k|§d§k|§e§k|");
                DuelsPlugin.get().getWinMelody().play(winner);
                DuelsPlugin.get().getDefeatMelody().play(looser);
            }
        }
    }
}
