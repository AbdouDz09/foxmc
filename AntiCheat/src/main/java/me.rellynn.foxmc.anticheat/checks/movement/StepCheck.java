package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class StepCheck extends Check {

    public StepCheck() {
        super("Step / FastFlight", 15, 0.2F);
    }

    private boolean shouldBypass(Player player, MovingData movingData) {
        return player.isFlying()
                || movingData.isAscending()
                || (player.hasPotionEffect(PotionEffectType.JUMP) && PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP) > 5);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (previous != null && current.getY() <= previous.getY() && !shouldBypass(player, movingData)) {
            double yMove = current.getY() - previous.getY();
            handleActions(yMove > 1.0D && yMove > movingData.serverVelocity.getY(), acPlayer, (float) yMove);
        }
    }
}
