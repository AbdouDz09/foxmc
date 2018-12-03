package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class WaterWalkCheck extends Check {

    public WaterWalkCheck() {
        super("WaterWalk", 8, 0.1F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (previous != null && !player.isInsideVehicle() && !player.isFlying() && movingData.isDescending()) {
            boolean wasHovering = isOverLiquid(previous),
                    isHovering = isOverLiquid(current);
            boolean wasOnLiquid = isOnLiquid(previous),
                    isOnLiquid = isOnLiquid(current);
            boolean failed = (previous.getY() % 1 <= 0.05F && wasHovering && current.getY() % 1 >= 0.95F && isOnLiquid) || (previous.getY() % 1 >= 0.95F && wasOnLiquid && current.getY() % 1 <= 0.05F && isHovering);
            handleActions(failed, acPlayer);
        }
    }

    private boolean isOverLiquid(Location location) {
        Block block = location.getBlock();
        return block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).isLiquid();
    }

    private boolean isOnLiquid(Location location) {
        Block block = location.getBlock();
        return block.isLiquid() && block.getRelative(BlockFace.UP).getType() == Material.AIR;
    }
}
