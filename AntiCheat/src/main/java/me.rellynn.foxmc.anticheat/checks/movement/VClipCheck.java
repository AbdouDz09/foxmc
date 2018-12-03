package me.rellynn.foxmc.anticheat.checks.movement;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 29/07/2017.
 * FoxMC Network.
 */
public class VClipCheck extends Check {

    public VClipCheck() {
        super("VClip", 5, 0.01F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Location previous = movingData.previousLocation,
                current = movingData.currentLocation;
        if (previous != null && previous.getBlockX() == current.getBlockX() && Math.abs(previous.getY() - current.getY()) >= 2.0D && previous.getBlockZ() == current.getBlockZ()) {
            int wrongMoves = 0;
            // Ray trace blocks
            for (int y = Math.min(previous.getBlockY(), current.getBlockY()); y < Math.max(previous.getBlockY(), current.getBlockY()); y++) {
                if (player.getWorld().getBlockAt(previous.getBlockX(), y, previous.getBlockZ()).getType().isSolid()) {
                    wrongMoves++;
                }
            }
            if (wrongMoves > 0) {
                // Increase the threshold by the number of blocks the player tried to pass through
                handleActions(true, acPlayer, wrongMoves);
                return;
            }
        }
        // Decrease the threshold
        handleActions(false, acPlayer);
    }
}
