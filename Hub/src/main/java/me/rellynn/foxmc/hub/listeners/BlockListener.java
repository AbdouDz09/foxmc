package me.rellynn.foxmc.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }
}
