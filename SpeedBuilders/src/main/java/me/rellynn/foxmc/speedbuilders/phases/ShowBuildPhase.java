package me.rellynn.foxmc.speedbuilders.phases;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.TimerPhase;
import me.rellynn.foxmc.gameapiv2.utils.BroadcastType;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.utils.Build;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import me.rellynn.foxmc.speedbuilders.utils.SBUtils;
import org.bukkit.Sound;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public class ShowBuildPhase extends TimerPhase<SBGame> {

    public ShowBuildPhase() {
        super(GameState.RUNNING, 200L);
    }

    @Override
    protected void onStart() {
        game.gotNewRound();
        Build build = game.getCurrentBuild();
        game.getAllPlayers().forEach(player -> {
            PacketUtils.sendTitle("", player);
            PacketUtils.sendSubTitle("§e" + build.getName(), player);
        });
        game.getPlatforms().forEach(platform -> {
            platform.clearBuild();
            build.paste(platform);
            platform.getEntity().sendMessage("§aLook the build carefully! §7You will have to reproduce it.");
        });
    }

    @Override
    protected void onTimerEvent(TimerEvent evt) {
        if (evt == TimerEvent.END) {
            game.broadcast(BroadcastType.SOUND, Sound.ANVIL_LAND);
            game.nextPhase();
            return;
        }
        // Timer is running
        int seconds = (int) (ticksLeft / 20);
        if (seconds <= 5) {
            String time = SBUtils.getFormattedTime(seconds);
            game.getAllPlayers().forEach(player -> {
                PacketUtils.sendTitle("", player);
                PacketUtils.sendSubTitle(time, player);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F - 0.07F * seconds);
            });
        }
    }

    @Override
    protected void onEnd() {
        game.getPlatforms().forEach(Platform::clearBuild);
    }
}
