package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 22/07/2017.
 * FoxMC Network.
 */
public class ImpossibleMoveCheck extends Check {

    public ImpossibleMoveCheck() {
        super("ImpossibleMove", 18, 1.5F);
    }

    public void check(Player player, ACPlayer acPlayer) {
        handleActions(player.isSprinting() && player.isSneaking(), acPlayer);
    }
}
