package me.rellynn.foxmc.anticheat.checks.blocks;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 27/07/2017.
 * FoxMC Network.
 */
public class FastPlaceCheck extends Check {
    private static final long MINIMUM_DELAY = 80L;

    public FastPlaceCheck() {
        super("FastPlace", 8, 0.5F);
    }

    public void check(Player player, ACPlayer acPlayer, Block block) {
        long now = System.currentTimeMillis(),
                diff = now - acPlayer.lastBlockPlaced;
        if (diff < 5L && block.getType() == Material.FIRE && player.getItemInHand() != null && player.getItemInHand().getType() == Material.FLINT_AND_STEEL) {
            // Bypass for double-call to BlockPlaceEvent with flint&steel
            return;
        }
        acPlayer.lastBlockPlaced = now;
        handleActions(diff < MINIMUM_DELAY, acPlayer);
    }
}
