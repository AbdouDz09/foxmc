package me.rellynn.foxmc.speedbuilders.scheduler;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.phases.BuildingPhase;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by gwennaelguich on 03/07/2017.
 * FoxMC Network.
 */
public class CheckPerfectBuild extends BukkitRunnable {
    private SBGame game = SBPlugin.getGame();

    private Platform<Player> platform;

    public CheckPerfectBuild(Platform<Player> platform) {
        this.platform = platform;
        runTaskLater(SBPlugin.get(), 3L);
    }

    @Override
    public void run() {
        Player player = platform.getEntity();
        if (!game.alreadyGotPerfect(platform) && Arrays.stream(player.getInventory().getContents()).noneMatch(Objects::nonNull) && game.getScore(platform) == 1.0F) {
            double time = (double) (System.currentTimeMillis() - ((BuildingPhase) game.getCurrentPhase()).getStartTime()) / 1000 - 0.15D;
            platform.setEditAllowed(false);
            game.increaseStatistic(player, "perfect_builds");
            game.giveTails(player, 1.5F, "perfect build");
            String message = player.getDisplayName() + " §7got a perfect build in §6" + String.format("%.1f", time) + " seconds§7!";
            PacketUtils.sendTitle("", player);
            PacketUtils.sendSubTitle("§aPerfect build!", player);
            player.playSound(player.getEyeLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
            game.getAllPlayers().forEach(gamePlayer -> {
                gamePlayer.sendMessage(message);
                if (gamePlayer != player)
                    gamePlayer.playSound(gamePlayer.getEyeLocation(), Sound.ORB_PICKUP, 0.8F, 1.0F);
            });
            game.gotPerfectBuild(platform);
        }
    }
}
