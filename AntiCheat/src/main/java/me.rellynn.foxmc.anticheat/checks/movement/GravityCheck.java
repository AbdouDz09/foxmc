package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 24/07/2017.
 * FoxMC Network.
 */
public class GravityCheck extends Check {
    private static final float MAXIMUM_DIFFERENCE = 0.01F;

    public GravityCheck() {
        super("Flight (Gravity)", 15, 0.1F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        long now = System.currentTimeMillis();
        return player.isInsideVehicle()
                || player.isFlying()
                || player.getFireTicks() > 0 // TODO: Maybe add bypass if the player has bad effect like poison?
                || movingData.hasVelocity()
                || movingData.justTeleported
                || movingData.wasOnLadder
                || movingData.isOnLadder
                || movingData.wasInLiquid
                || movingData.isInLiquid
                || movingData.isInCobweb
                || now - movingData.lastStairsOrSlab < 500L;
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (!movingData.onGround && movingData.previousYDiff < 0.0D && current.getY() <= previous.getY() && movingData.isDescending() && !shouldBypass(player, movingData)) {
            double actualDiff = current.getY() - previous.getY(),
                    expectedDiff = Math.min(3.92D, (movingData.previousYDiff - 0.08D) * 0.98D);
            if (Math.abs(expectedDiff - actualDiff) > MAXIMUM_DIFFERENCE) {
                float increaseAmount = System.currentTimeMillis() - movingData.lastJump < 500L ? 0.5F : 1.0F;
                handleActions(true, acPlayer, increaseAmount);
                return;
            }
        }
        // Decrease the threshold
        handleActions(false, acPlayer);
    }
}
