package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 22/07/2017.
 * FoxMC Network.
 */
public class SpeedCheck extends Check {
    private static final float ALLOWED_DIFFERENCE = 0.15F;

    public SpeedCheck() {
        super("Speed", 18, 0.2F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return player.isInsideVehicle()
                || player.getAllowFlight()
                || movingData.hasVelocity()
                || movingData.isOnLadder;
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (previous != null && !shouldBypass(player, movingData)) {
            double xMove = current.getX() - previous.getX(),
                    zMove = current.getZ() - previous.getZ();
            double distance = xMove * xMove + zMove * zMove;
            float maxDistance = movingData.getMaxHorizontalDistance() * (1 + ALLOWED_DIFFERENCE);
            handleActions(distance > maxDistance, acPlayer);
        }
    }
}
