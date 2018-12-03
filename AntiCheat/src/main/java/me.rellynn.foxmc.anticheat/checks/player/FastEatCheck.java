package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class FastEatCheck extends Check {
    private static final long MINIMUM_TIME_DIFFERENCE = 1200L;

    public FastEatCheck() {
        super("FastEat", 8);
    }

    public boolean passCheck(Player player, ACPlayer acPlayer) {
        return handleActions(System.currentTimeMillis() - acPlayer.lastInteractTime < MINIMUM_TIME_DIFFERENCE, acPlayer);
    }
}
