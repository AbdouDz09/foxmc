package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class VelocityCheck extends Check {

    public VelocityCheck() {
        super("Velocity / AntiKb", 8, 1.5F);
    }

    public void check(Player player, ACPlayer acPlayer, double velY, double reachedY) {
        // Took from: https://github.com/hugo4715/KnockbackPlusPlus/blob/master/src/main/java/tk/hugo4715/anticheat/check/KbChecker.java
        double exceptedY = (0.00000008D * velY * velY) + (0.0001D * velY) - 0.0219D;
        handleActions(reachedY < exceptedY, acPlayer);
    }
}
