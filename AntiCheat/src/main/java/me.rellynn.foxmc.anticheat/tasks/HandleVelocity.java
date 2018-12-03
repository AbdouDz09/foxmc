package me.rellynn.foxmc.anticheat.tasks;

import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.player.VelocityCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class HandleVelocity extends BukkitRunnable {
    private static final VelocityCheck VELOCITY_CHECK = new VelocityCheck();

    private Player player;
    private ACPlayer acPlayer;
    private MovingData data;
    private int velX, velY, velZ;
    private long accountTick;
    private long checkTick = -1L;

    private long ticks;
    private double reachedY;
    private double baseY;

    public HandleVelocity(Player player, int velX, int velY, int velZ) {
        this.player = player;
        this.acPlayer = ACPlayer.get(player);
        this.data = acPlayer.movingData;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        int ping = acPlayer.getPing();
        this.accountTick = Math.round(ping / (double) 50);
        if (velY > 500 && velY < 5000) {
            this.checkTick = (long) Math.ceil((double) (ping + 150) / (double) 20);
        }
        this.baseY = data.currentLocation.getY();
        runTaskTimer(ACPlugin.get(), 0L, 1L);
    }

    private boolean shouldBypass() {
        return ticks > checkTick
                || !data.onGround
                || data.isOnLadder
                || data.isInLiquid
                || data.isInCobweb
                || data.hasCeiling
                || player.isInsideVehicle()
                || player.isFlying()
                || player.getFireTicks() > 0;
    }

    @Override
    public void run() {
        if (!player.isOnline() || player.isDead()) {
            cancel();
            return;
        }
        if (ticks == accountTick) {
            data.addVelocity(velX / (double) 8000, velZ / (double) 800);
            if (checkTick == -1L || shouldBypass()) {
                cancel();
                return;
            }
        }
        if (checkTick != -1L) {
            double y = data.currentLocation.getY();
            if (y > reachedY) {
                reachedY = y;
            }
            if (ticks == checkTick) {
                cancel();
                VELOCITY_CHECK.check(player, acPlayer, velY, reachedY - baseY);
            }
        }
        ticks++;
    }
}
