package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class RegenCheck extends Check {
    private static final long MINIMUM_TIME_DIFFERENCE = 3600L;

    public RegenCheck() {
        super("Regen", 12);
    }

    public boolean passCheck(Player player, ACPlayer acPlayer) {
        return handleActions(System.currentTimeMillis() - acPlayer.lastRegenTime < MINIMUM_TIME_DIFFERENCE, acPlayer);
    }
}
