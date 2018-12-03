package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class AirJumpCheck extends Check {

    public AirJumpCheck() {
        super("AirJump", 6, 0.05F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return player.isInsideVehicle()
                || player.getAllowFlight()
                || player.getFireTicks() > 0
                || movingData.onGround
                || movingData.isOnLadder
                || movingData.isInLiquid
                || movingData.hasVelocity()
                || movingData.isAscending()
                || !movingData.isFalling()
                || System.currentTimeMillis() - movingData.lastStairsOrSlab < 500L;
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location current = movingData.currentLocation,
                previous = movingData.previousLocation;
        if (previous != null && current.getY() > previous.getY() && !shouldBypass(player, movingData)) {
            handleActions(true, acPlayer);
            return;
        }
        // Decrease the threshold
        handleActions(false, acPlayer);
    }
}
