package me.rellynn.foxmc.anticheat.listeners;

import me.rellynn.foxmc.anticheat.checks.blocks.FastPlaceCheck;
import me.rellynn.foxmc.anticheat.checks.blocks.LiquidsCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.tasks.DelayNoSwingCheck;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class BlockListener implements Listener {
    private static final LiquidsCheck LIQUIDS_CHECK = new LiquidsCheck();
    private static final FastPlaceCheck FAST_PLACE_CHECK = new FastPlaceCheck();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreBlockBreak(BlockBreakEvent evt) {
        if (!LIQUIDS_CHECK.passCheck(evt.getPlayer(), ACPlayer.get(evt.getPlayer()), evt.getBlock())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent evt) {
        new DelayNoSwingCheck(evt.getPlayer(), ACPlayer.get(evt.getPlayer())); // Delay NoSwing check (fix lag issues)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreBlockPlace(BlockPlaceEvent evt) {
        if (!LIQUIDS_CHECK.passCheck(evt.getPlayer(), ACPlayer.get(evt.getPlayer()), evt.getBlock(), evt.getBlockAgainst())) {
            evt.setCancelled(true);
        }
        FAST_PLACE_CHECK.check(evt.getPlayer(), ACPlayer.get(evt.getPlayer()), evt.getBlock());
    }
}
