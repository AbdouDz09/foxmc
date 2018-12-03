package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class NoFallCheck extends Check {
    private static final float NO_FALL_TOLERANCE = 0.1F;

    public NoFallCheck() {
        super("NoFall");
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return movingData.fallDistance <= 3.0F
                || movingData.fallDistance <= player.getFallDistance()
                || player.getGameMode() == GameMode.CREATIVE
                || player.isFlying();
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        double diff = Math.min(movingData.fallDistance, player.getFallDistance()) / Math.max(movingData.fallDistance, player.getFallDistance());
        if (movingData.onGround && diff < (1 - NO_FALL_TOLERANCE) && !shouldBypass(player, movingData)) {
            player.setFallDistance(movingData.fallDistance);
        }
        movingData.fallDistance = 0.0F;
    }
}
