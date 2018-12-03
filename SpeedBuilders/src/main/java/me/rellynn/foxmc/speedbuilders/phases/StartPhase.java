package me.rellynn.foxmc.speedbuilders.phases;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.TimerPhase;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.utils.SBUtils;
import org.bukkit.Sound;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public class StartPhase extends TimerPhase<SBGame> {

    public StartPhase() {
        super(GameState.RUNNING, 200L, 5L);
    }

    @Override
    protected void onStart() {
        game.givePlatforms();
        game.getAllPlayers().forEach(player -> game.createScoreboard(player));
    }

    @Override
    protected void onTimerEvent(TimerEvent evt) {
        if (evt == TimerEvent.END) {
            game.nextPhase();
            return;
        }
        // Timer is running
        int seconds = (int) Math.round((double) ticksLeft / 20);
        float progress = (float) ticksLeft / totalTicks;
        String time = SBUtils.getFormattedTime(seconds);
        String message = "§eGame will start in " + time + " §esecond" + (seconds != 1 ? "s" : "") + ".";
        boolean printMessage = ticksLeft % 20 == 0 && (seconds % 5 == 0 || seconds < 5);
        game.getAllPlayers().forEach(player -> {
            player.setLevel(seconds);
            player.setExp(progress);
            if (printMessage) {
                player.sendMessage(message);
                PacketUtils.sendTitle("", player);
                PacketUtils.sendSubTitle(time, player);
                player.playSound(player.getEyeLocation(), Sound.NOTE_STICKS, 0.8F, 1.5F - 0.07F * seconds);
            }
        });
    }

    @Override
    protected void onEnd() {
        game.getAllPlayers().forEach(player -> {
            player.setLevel(0);
            player.setExp(0.0F);
            player.playSound(player.getEyeLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
        });
    }
}
