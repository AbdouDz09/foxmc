package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 07/08/2017.
 * FoxMC Network.
 */
public class AimbotCheck extends Check {
    private static final float MINIMUM_DISTANCE = 2.0F;
    private static final double MIN_DISTANCE_SQUARED = Math.pow(MINIMUM_DISTANCE, 2);
    private static final float HEAD_TARGET_Y = 0.0F;
    private static final float CORPSE_TARGET_Y = 0.65F;
    private static final float ALLOWED_Y_DIFFERENCE = 0.145F;
    private static final float ALLOWED_XZ_DIFFERENCE = 0.135F;

    public AimbotCheck() {
        super("Aimbot", 9, 0.5F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData, LivingEntity attacked) {
        if (movingData.currentLocation.distanceSquared(attacked.getLocation()) >= MIN_DISTANCE_SQUARED) {
            Location eyeLocation = player.getEyeLocation(),
                    attackedEyeLocation = attacked.getEyeLocation();
            double distance = eyeLocation.distance(attackedEyeLocation);
            Location diff = player.getEyeLocation().add(eyeLocation.getDirection().normalize().multiply(distance)).subtract(attackedEyeLocation);
            boolean xFailed = Math.abs(diff.getX()) <= ALLOWED_XZ_DIFFERENCE,
                    yFailed = (Math.abs(diff.getY()) <= HEAD_TARGET_Y + ALLOWED_Y_DIFFERENCE && Math.abs(diff.getY()) >= HEAD_TARGET_Y - ALLOWED_Y_DIFFERENCE) || (Math.abs(diff.getY()) <= CORPSE_TARGET_Y + ALLOWED_Y_DIFFERENCE && Math.abs(diff.getY()) >= CORPSE_TARGET_Y - ALLOWED_Y_DIFFERENCE),
                    zFailed = Math.abs(diff.getZ()) <= ALLOWED_XZ_DIFFERENCE;
            handleActions(xFailed && yFailed && zFailed, acPlayer);
        }
    }
}
