package me.rellynn.foxmc.anticheat.listeners;

import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.player.FastEatCheck;
import me.rellynn.foxmc.anticheat.checks.player.RegenCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.utils.BlockUtils;
import me.rellynn.foxmc.anticheat.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private static final RegenCheck REGEN_CHECK = new RegenCheck();
    private static final FastEatCheck FAST_EAT_CHECK = new FastEatCheck();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        ACPlugin.get().initPlayer(evt.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        // Reset check for NoSlowDown hack
        ACPlayer.get(evt.getEntity()).checkSlowdown = false;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        // Reset check for NoSlowDown hack
        ACPlayer.get(evt.getPlayer()).checkSlowdown = false;
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent evt) {
        // Reset check for NoSlowDown hack
        ACPlayer.get(evt.getPlayer()).checkSlowdown = false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.hasItem() && (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ACPlayer acPlayer = ACPlayer.get(evt.getPlayer());
            acPlayer.lastInteractTime = System.currentTimeMillis();
            if (!evt.hasBlock() || !BlockUtils.isInteractable(evt.getClickedBlock()) || evt.getPlayer().isSneaking()) {
                acPlayer.checkSlowdown = PlayerUtils.canSlowDown(evt.getPlayer());
                if (acPlayer.checkSlowdown) {
                    acPlayer.checkSlowdownStart = System.currentTimeMillis();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent evt) {
        ACPlayer acPlayer = ACPlayer.get(evt.getPlayer());
        if (!FAST_EAT_CHECK.passCheck(evt.getPlayer(), acPlayer)) {
            evt.setCancelled(true);
        }
        // Reset check for NoSlowDown hack
        acPlayer.checkSlowdown = false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRegen(EntityRegainHealthEvent evt) {
        if (evt.getEntity() instanceof Player && evt.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            ACPlayer acPlayer = ACPlayer.get((Player) evt.getEntity());
            if (!REGEN_CHECK.passCheck((Player) evt.getEntity(), acPlayer)) {
                evt.setCancelled(true);
                return;
            }
            acPlayer.lastRegenTime = System.currentTimeMillis();
        }
    }
}
