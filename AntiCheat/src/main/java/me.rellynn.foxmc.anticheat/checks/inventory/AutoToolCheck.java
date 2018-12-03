package me.rellynn.foxmc.anticheat.checks.inventory;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class AutoToolCheck extends Check {

    public AutoToolCheck() {
        super("AutoTool", 6);
    }

    public void check(Player player, ACPlayer acPlayer) {
        handleActions(acPlayer.lastItemInHand != null && player.getItemInHand() != null && acPlayer.lastItemInHand.getType() != player.getItemInHand().getType(), acPlayer);
    }
}
