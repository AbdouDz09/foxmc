package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 29/07/2017.
 * FoxMC Network.
 */
public class ConsistencyCheck extends Check {
    public static final int DATA_VALIDATION = 20;
    private static final int MAXIMUM_DATA_ERRORS = 2;
    private static final float MAXIMUM_DATA_DIFFERENCE = 0.05F;

    public ConsistencyCheck() {
        super("AutoClicker (Consistency)", 5, 5.0F);
    }

    public void check(Player player, ACPlayer acPlayer, FightData fightData) {
        int samplesCount = fightData.previousAttacks.size();
        if (samplesCount >= DATA_VALIDATION) {
            long average = fightData.previousAttacks.stream().reduce(0L, Long::sum) / samplesCount,
                    errors = fightData.previousAttacks.stream().filter(value -> Math.min(value, average) / (double) Math.max(value, average) < (1 - MAXIMUM_DATA_DIFFERENCE)).count();
            handleActions(errors <= MAXIMUM_DATA_ERRORS, acPlayer);
            fightData.previousAttacks.clear();
        }
    }
}
