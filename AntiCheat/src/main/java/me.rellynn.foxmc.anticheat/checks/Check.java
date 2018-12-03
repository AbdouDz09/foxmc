package me.rellynn.foxmc.anticheat.checks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.anticheat.ACConfig;
import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public abstract class Check {
    private String name;
    private int maxThreshold;
    private float safeDecrease;

    public Check(String name, int maxThreshold) {
        this(name, maxThreshold, 1.0F);
    }

    public Check(String name) {
        this(name, 0);
    }

    protected boolean handleActions(boolean failed, ACPlayer acPlayer, float amount) {
        if (!failed) {
            if (safeDecrease != 0.0F) {
                acPlayer.decreaseThreshold(this, amount);
            }
        } else {
            acPlayer.increaseThreshold(this, amount);
            float threshold = acPlayer.getThreshold(this);
            if (ACConfig.DEBUG_MODE) {
                System.out.println("(" + acPlayer.player.getName() + ") §cFailed §r" + name + " §ccheck! §r(threshold=" + threshold + ", ping=" + acPlayer.getPing() + ")");
            }
            if (maxThreshold != 0 && threshold >= maxThreshold) {
                if (Bukkit.isPrimaryThread()) {
                    acPlayer.failedCheck(this, threshold);
                } else {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            acPlayer.failedCheck(Check.this, threshold);
                        }
                    }.runTask(ACPlugin.get());
                }
            }
        }
        return !failed;
    }

    protected boolean handleActions(boolean failed, ACPlayer acPlayer) {
        return handleActions(failed, acPlayer, failed ? 1.0F : safeDecrease);
    }
}
