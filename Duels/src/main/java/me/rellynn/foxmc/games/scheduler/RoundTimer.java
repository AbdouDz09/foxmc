package me.rellynn.foxmc.games.scheduler;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.Scoreboard;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.DuelGameInfo;
import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 01/05/2017.
 * FoxMC Network.
 */
public class RoundTimer extends BukkitRunnable {
    private Game game;
    private DuelGameInfo duelInfo;
    private int totalSec;
    @Getter private boolean running;

    public RoundTimer(Game game, DuelGameInfo duelInfo) {
        this.game = game;
        this.duelInfo = duelInfo;
        this.reset();
        runTaskTimer(DuelsPlugin.get(), 0, 20);
    }

    /**
     * Start the round timer.
     */
    public void start() {
        this.running = true;
    }

    /**
     * Reset the round timer and stop it.
     * Also, the scoreboards are updated.
     */
    public void reset() {
        this.running = false;
        this.totalSec = 180;
        updateScoreboards();
    }

    private void updateScoreboards() {
        game.getAllPlayers().forEach(player -> {
            Scoreboard board = game.getScoreboard(player);
            if (board == null) {
                board = game.createScoreboard(player);
                board.setLine(0, "");
                board.setLine(1, "§8» §7Players:");
                board.setLine(2, duelInfo.getFirstPlayer().getDisplayName());
                board.setLine(3, "       vs");
                board.setLine(4, duelInfo.getSecondPlayer().getDisplayName());
                board.setLine(5, "");
                board.setLine(8, "");
                board.setLine(9, "§eplay.foxmc.net");
            }
            int minutes = (int) Math.floor(totalSec / 60);
            int seconds = totalSec % 60;
            board.setLine(7, "§a" + (minutes < 10 ? "0" : "") + minutes + "m" + (seconds < 10 ? "0" : "") + seconds + "s left");
            board.setLine(6, "§8» §7Round §f" + duelInfo.getPlayedRounds() + "§7/" + duelInfo.getMaxRounds() + ":");
        });
    }

    @Override
    public void run() {
        if (!running) {
            return;
        } else if (totalSec <= 0) {
            game.broadcast("§cRound time is over. §e§oStarting new round...");
            game.getPlayers().forEach(player -> player.teleport(game.getFreeSpawn()));
            duelInfo.stopRoundTimer();
            duelInfo.startRound();
            return;
        }
        updateScoreboards();
        totalSec--;
    }
}
