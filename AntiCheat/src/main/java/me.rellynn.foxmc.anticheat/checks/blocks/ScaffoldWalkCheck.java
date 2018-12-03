package me.rellynn.foxmc.anticheat.checks.blocks;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 28/07/2017.
 * FoxMC Network.
 */
public class ScaffoldWalkCheck extends Check {

    public ScaffoldWalkCheck() {
        super("ScaffoldWalk", 8, 0.5F);
    }

    public void check(Player player, ACPlayer acPlayer) {
        // TODO: Rework
        handleActions(false, acPlayer);
    }
}
