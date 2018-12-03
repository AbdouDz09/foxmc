package me.rellynn.foxmc.anticheat.listeners;

import me.rellynn.foxmc.anticheat.checks.inventory.AutoToolCheck;
import me.rellynn.foxmc.anticheat.checks.inventory.ImpossibleClickCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Created by gwennaelguich on 22/07/2017.
 * FoxMC Network.
 */
public class InventoryListener implements Listener {
    private static final ImpossibleClickCheck IMPOSSIBLE_CLICK_CHECK = new ImpossibleClickCheck();
    private static final AutoToolCheck AUTO_TOOL_CHECK = new AutoToolCheck();

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent evt) {
        ACPlayer.get((Player) evt.getPlayer()).inventoryOpened = true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        ACPlayer.get((Player) evt.getPlayer()).inventoryOpened = false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent evt) {
        IMPOSSIBLE_CLICK_CHECK.check((Player) evt.getWhoClicked(), ACPlayer.get((Player) evt.getWhoClicked()), evt.getClick(), evt.getAction(), evt.getClickedInventory());
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent evt) {
        AUTO_TOOL_CHECK.check(evt.getPlayer(), ACPlayer.get(evt.getPlayer()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            AUTO_TOOL_CHECK.check((Player) evt.getDamager(), ACPlayer.get((Player) evt.getDamager()));
        }
    }
}
