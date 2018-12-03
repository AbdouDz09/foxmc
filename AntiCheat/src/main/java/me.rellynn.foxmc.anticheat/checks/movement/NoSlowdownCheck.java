package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 03/08/2017.
 * FoxMC Network.
 */
public class NoSlowdownCheck extends Check {
    private static final float MAXIMUM_SPEED = 0.045F;

    public NoSlowdownCheck() {
        super("NoSlowdown", 18, 0.25F);
    }

    private boolean shouldBypass(Player player, ACPlayer acPlayer, MovingData movingData) {
        long now = System.currentTimeMillis();
        return player.isInsideVehicle()
                || player.getFireTicks() > 0
                || movingData.hasVelocity()
                || now - acPlayer.checkSlowdownStart < 750L
                || BlockUtils.getFrictionFactor(movingData.currentLocation.getBlock().getRelative(BlockFace.DOWN)) > 0.91F;
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (previous != null && !shouldBypass(player, acPlayer, movingData)) {
            if (acPlayer.checkSlowdown) {
                double distanceSquared = Math.pow(current.getX() - previous.getX(), 2) + Math.pow(current.getZ() - previous.getZ(), 2);
                handleActions(distanceSquared > MAXIMUM_SPEED, acPlayer);
                return;
            }
            // Decrease threshold in case of good move
            handleActions(false, acPlayer);
        }
    }
}
