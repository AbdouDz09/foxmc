package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 27/07/2017.
 * FoxMC Network.
 */
public class AutoClickerCheck extends Check {
    private static final int MAXIMUM_ATTACKS_PER_SECOND = 15;

    public AutoClickerCheck() {
        super("AutoClicker", 8, 8.0F);
    }

    public void check(Player player, ACPlayer acPlayer, FightData fightData) {
        long now = System.currentTimeMillis();
        if (now - fightData.countAttacksStart >= 1000L) {
            boolean failed = fightData.attacksCount > MAXIMUM_ATTACKS_PER_SECOND;
            fightData.attacksCount = 0;
            fightData.countAttacksStart = now;
            handleActions(failed, acPlayer);
        }
        fightData.attacksCount++;
    }
}
