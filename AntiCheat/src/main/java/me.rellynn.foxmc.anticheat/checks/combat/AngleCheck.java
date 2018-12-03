package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 29/07/2017.
 * FoxMC Network.
 */
public class AngleCheck extends Check {
    private static final float MINIMUM_DISTANCE = 2.0F;
    private static final double MIN_DISTANCE_SQUARED = Math.pow(MINIMUM_DISTANCE, 2);
    private static final float MAXIMUM_ALLOWED_ANGLE = 1.25F;

    public AngleCheck() {
        super("KillAura (Angle)", 8);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData, Entity attacked) {
        if (movingData.currentLocation.distanceSquared(attacked.getLocation()) >= MIN_DISTANCE_SQUARED) {
            double angle = attacked.getLocation().toVector().subtract(movingData.currentLocation.toVector()).angle(movingData.currentLocation.getDirection());
            handleActions(angle > MAXIMUM_ALLOWED_ANGLE, acPlayer);
        }
    }
}
