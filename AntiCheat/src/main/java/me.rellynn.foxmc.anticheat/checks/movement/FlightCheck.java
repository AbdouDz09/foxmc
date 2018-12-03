package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 19/07/2017.
 * FoxMC Network.
 */
public class FlightCheck extends Check {
    private static final float ALLOWED_DIFFERENCE = 0.05F;

    public FlightCheck() {
        super("Flight", 10, 0.05F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return player.isInsideVehicle()
                || player.getAllowFlight()
                || player.getFireTicks() > 0
                || movingData.isOnLadder
                || movingData.isInLiquid
                || movingData.hasVelocity()
                || System.currentTimeMillis() - movingData.lastLiquidSurface < 500L;
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation,
                ground = movingData.lastGroundLocation;
        if (!movingData.onGround && previous != null && ground != null && !shouldBypass(player, movingData)) {
            double heightFromGround = current.getY() - ground.getY();
            float maxJump = movingData.getMaxVerticalDistance() * (1 + ALLOWED_DIFFERENCE);
            if (heightFromGround > maxJump && current.getY() > movingData.highestY) {
                // Player failed Fly check
                float increaseAmount = (movingData.clientOnGround) ? 1.5F : 1.0F; // We're more strict if the player sends fake ground data :)
                handleActions(true, acPlayer, increaseAmount);
                return;
            }
        }
        // Decrease the threshold
        handleActions(false, acPlayer);
    }
}
