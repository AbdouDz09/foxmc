package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.ACConfig;
import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class FastLadderCheck extends Check {
    private static final float LADDER_SPEED = 0.118F;
    private static final float LADDER_START_SPEED = 0.42F;

    public FastLadderCheck() {
        super("FastLadder", 10, 0.25F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return player.isFlying() || movingData.hasVelocity();
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation,
                ground = movingData.lastGroundLocation;
        if (movingData.isOnLadder && ground != null && previous != null && current.getY() > previous.getY() && !shouldBypass(player, movingData)) {
            double yMove = current.getY() - previous.getY(),
                    maxYMove = current.getY() - ground.getY() <= ACConfig.BASE_JUMP_HEIGHT ? LADDER_START_SPEED : LADDER_SPEED;
            handleActions(yMove > maxYMove, acPlayer);
        }
    }
}
