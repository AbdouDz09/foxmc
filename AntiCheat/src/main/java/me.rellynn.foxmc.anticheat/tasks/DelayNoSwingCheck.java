package me.rellynn.foxmc.anticheat.tasks;

import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.player.NoSwingCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 25/07/2017.
 * FoxMC Network.
 */
public class DelayNoSwingCheck extends BukkitRunnable {
    private static final NoSwingCheck NO_SWING_CHECK = new NoSwingCheck();

    private Player player;
    private ACPlayer acPlayer;
    private FightData fightData;
    private long startCheckTime;
    private long ticks;

    public DelayNoSwingCheck(Player player, ACPlayer acPlayer) {
        this.player = player;
        this.acPlayer = acPlayer;
        this.fightData = acPlayer.fightData;
        this.startCheckTime = System.currentTimeMillis();
        runTaskTimer(ACPlugin.get(), 1L, 1L);
    }

    @Override
    public void run() {
        ticks++;
        boolean isLastTry = ticks > NoSwingCheck.NO_SWING_LENIENCY;
        if (NO_SWING_CHECK.passCheck(player, acPlayer, fightData, startCheckTime, isLastTry) || isLastTry) {
            cancel();
        }
    }
}
