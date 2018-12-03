package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 27/07/2017.
 * FoxMC Network.
 */
public class ReachCheck extends Check {
    private static final float ALLOWED_REACH = 4.7F;
    private static final double ALLOWED_REACH_SQUARED = Math.pow(ALLOWED_REACH, 2);
    private static final float ALLOWED_REACH_DIFFERENCE = 0.15F;

    public ReachCheck() {
        super("KillAura (Reach)", 8, 1.5F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData, FightData fightData, Entity attacked) {
        if (fightData.lastRodEntity == null || attacked != fightData.lastRodEntity.get()) {
            Location current = movingData.currentLocation,
                    target = attacked.getLocation().subtract(attacked.getVelocity());
            double reachSquared = Math.pow(current.getX() - target.getX(), 2) + Math.pow(current.getZ() - target.getZ(), 2),
                    reachOffset = reachSquared - ALLOWED_REACH_SQUARED;
            if (reachOffset > (ALLOWED_REACH_DIFFERENCE + PlayerUtils.getPotionLevel(player, PotionEffectType.SPEED) / (double) 10)) {
                handleActions(true, acPlayer);
                return;
            }
            handleActions(false, acPlayer);
        }
    }
}
