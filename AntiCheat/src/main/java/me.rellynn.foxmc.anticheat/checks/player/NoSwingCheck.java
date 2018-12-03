package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 19/07/2017.
 * FoxMC Network.
 */
public class NoSwingCheck extends Check {
    public static final long NO_SWING_LENIENCY = 6L;
    private static final long MAX_TIME_DIFFERENCE = 80L;

    public NoSwingCheck() {
        super("NoSwing", 8);
    }

    public boolean passCheck(Player player, ACPlayer acPlayer, FightData fightData, long startCheckTime, boolean increaseVL) {
        boolean failed = startCheckTime - fightData.lastSwingTime > MAX_TIME_DIFFERENCE;
        return (!failed || increaseVL) && handleActions(failed, acPlayer);
    }
}
