package me.rellynn.foxmc.anticheat.tasks;

import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.movement.GravityCheck;
import me.rellynn.foxmc.anticheat.checks.player.AntiHungerCheck;
import me.rellynn.foxmc.anticheat.checks.player.AntiPotionCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class TickPlayer extends BukkitRunnable {
    private static final GravityCheck GRAVITY_CHECK = new GravityCheck();
    private static final AntiPotionCheck ANTI_POTION_CHECK = new AntiPotionCheck();
    private static final AntiHungerCheck ANTI_HUNGER_CHECK = new AntiHungerCheck();

    private Player player;
    private ACPlayer acPlayer;
    private MovingData data;

    private long ticks;

    public TickPlayer(Player player, ACPlayer acPlayer, MovingData movingData) {
        this.player = player;
        this.acPlayer = acPlayer;
        this.data = movingData;
        runTaskTimer(ACPlugin.get(), 0L, 1L);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }
        ticks++;
        acPlayer.updatePing();
        data.updateClientVelocity();
        GRAVITY_CHECK.check(player, acPlayer, data);
        if (ticks % 20 == 0L) {
            ANTI_POTION_CHECK.check(player, acPlayer, data);
            ANTI_HUNGER_CHECK.check(player, acPlayer, data);
            data.packetsCount = 0;
        }
        // Update moving data
        if (data.fallDistance != 0.0F && data.isInLava) {
            // Decrease fall distance by half each tick while in lava
            data.fallDistance *= 0.5F;
        }
    }
}
