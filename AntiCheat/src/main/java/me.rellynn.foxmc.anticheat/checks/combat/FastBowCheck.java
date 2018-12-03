package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class FastBowCheck extends Check {
    private static final float MAXIMUM_FORCE_DIFFERENCE = 0.25F;

    public FastBowCheck() {
        super("FastBow", 8);
    }

    public boolean passCheck(Player player, ACPlayer acPlayer, float force) {
        int ticks = (int) ((((System.currentTimeMillis() - acPlayer.lastInteractTime) * 20L) / 1000L) + 3);
        float f = (float) ticks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        f = Math.min(f, 1.0F);
        f = Math.abs(force - f);
        return handleActions(f > MAXIMUM_FORCE_DIFFERENCE, acPlayer);
    }
}
