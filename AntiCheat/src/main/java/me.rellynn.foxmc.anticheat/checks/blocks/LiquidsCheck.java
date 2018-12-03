package me.rellynn.foxmc.anticheat.checks.blocks;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class LiquidsCheck extends Check {

    public LiquidsCheck() {
        super("Liquids");
    }

    public boolean passCheck(Player player, ACPlayer acPlayer, Block block) {
        return !block.isLiquid();
    }

    public boolean passCheck(Player player, ACPlayer acPlayer, Block placed, Block against) {
        return !against.isLiquid() || (against.getType() == Material.WATER && placed.getType() == Material.WATER_LILY);
    }
}
