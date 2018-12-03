package me.rellynn.foxmc.gameapiv2.games.phases;

import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class WaitingPhase extends Phase {
    private int totalSeconds, seconds;
    private int dots;

    public WaitingPhase(int seconds) {
        super(GameState.WAITING);
        this.totalSeconds = seconds;
        this.seconds = seconds;
        setTicking(20);
    }

    public WaitingPhase() {
        this(30);
    }

    private void updateState(String newText) {
        game.getAllPlayers().forEach(player -> game.getScoreboard(player).setLine(4, newText));
    }

    @Override
    protected void onTick() {
        int players = game.getPlayers().size();
        if (players < game.getArena().getMinPlayers()) {
            if (totalSeconds == seconds) {
                updateState("§7Waiting players" + StringUtils.repeat(".", dots++));
                if (dots > 3) {
                    dots = 0;
                }
            } else {
                seconds = totalSeconds;
                String message = "§cThere is not enough players. §7The timer is stopped.";
                game.getAllPlayers().forEach(player -> {
                    player.sendMessage(message);
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.5f, 1);
                    game.getScoreboard(player).setLine(4, "§cTimer stopped");
                });
            }
        } else {
            if (seconds <= 0) {
                game.start();
                return;
            }
            updateState("§aStarting in " + seconds + "s");
            if (seconds == totalSeconds || seconds % 15 == 0 || seconds == 10 || seconds <= 5) {
                String message = String.format("§6Starting game in §e%s second%s.", seconds, seconds > 1 ? "s" : "");
                game.getAllPlayers().forEach(player -> {
                    player.sendMessage(message);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
                });
            }
            seconds--;
        }
    }

    @Override
    protected void onEnd() {
        game.getPlayers().forEach(PlayerUtils::resetPlayer);
    }
}
