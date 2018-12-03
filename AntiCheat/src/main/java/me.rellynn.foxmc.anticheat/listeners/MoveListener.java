package me.rellynn.foxmc.anticheat.listeners;

import me.rellynn.foxmc.anticheat.checks.movement.NoFallCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.tasks.HandleMoveAsync;
import me.rellynn.foxmc.anticheat.utils.BlockUtils;
import me.rellynn.foxmc.anticheat.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class MoveListener implements Listener {
    private static final NoFallCheck NO_FALL_CHECK = new NoFallCheck();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent evt) {
        ACPlayer.get(evt.getEntity()).movingData.fallDistance = 0.0F;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent evt) {
        ACPlayer.get(evt.getPlayer()).movingData.fallDistance = 0.0F;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerVelocity(PlayerVelocityEvent evt) {
        ACPlayer.get(evt.getPlayer()).movingData.serverVelocity = evt.getVelocity();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent evt) {
        if (!evt.isFlying()) {
            ACPlayer.get(evt.getPlayer()).movingData.lastGroundLocation = null;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        MovingData data = ACPlayer.get(evt.getPlayer()).movingData;
        data.updateMovingData(evt.getRespawnLocation().clone(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        MovingData data = ACPlayer.get(evt.getPlayer()).movingData;
        data.updateMovingData(evt.getTo().clone(), true);
        if (evt.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            data.fallDistance = 0.0F;
        }
    }

    /*
    Move
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreMove(PlayerMoveEvent evt) {
        ACPlayer.get(evt.getPlayer()).movingData.nextLocation = evt.getTo().clone();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        Location from = evt.getFrom(),
                to = evt.getTo();
        Player player = evt.getPlayer();
        if (player.isDead()) {
            return;
        }
        ACPlayer acPlayer = ACPlayer.get(player);
        MovingData data = acPlayer.movingData;
        if (!data.nextLocation.equals(to)) {
            // Let PlayerTeleportEvent manage data update
            return;
        }
        data.updateMovingData(to.clone(), false);
        if (player.isFlying()
                || data.isAscending()
                || (data.isDescending() && to.getY() < data.highestY)
                || data.onGround
                || data.wasOnLadder
                || data.isOnLadder
                || data.wasInLiquid
                || data.isInLiquid) {
            data.highestY = to.getY();
        }
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            // Update moving time values
            long now = System.currentTimeMillis();
            if (player.isSneaking()) {
                data.lastSneak = now;
            } else if (player.isSprinting()) {
                data.lastSprint = now;
            } else {
                data.lastWalk = now;
            }
            if (!data.onGround && data.wasOnGround && to.getY() > from.getY()) {
                data.lastJump = now;
            }
            if (data.isInLiquid && to.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                data.lastLiquidSurface = now;
            }
            Block ground = to.getBlock().getRelative(BlockFace.DOWN);
            if (ground.getType() == Material.ICE || ground.getType() == Material.PACKED_ICE) {
                data.lastOnIce = now;
            }
            long lastStairsOrSlab = data.lastStairsOrSlab;
            if (from.getY() != to.getY() && (BlockUtils.isSlab(to.getBlock()) || BlockUtils.isStairs(to.getBlock()) || BlockUtils.isStairs(ground))) {
                data.lastStairsOrSlab = now;
            }
            if (from.getBlockY() == to.getBlockY() && data.hasCeiling) {
                data.lastHasCeiling = now;
            }
            // Potion effects
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                data.lastSpeedLvl = PlayerUtils.getPotionLevel(player, PotionEffectType.SPEED);
                data.lastSpeedEffect = now;
            }
            // Fall distance
            if (player.isInsideVehicle() || data.isOnLadder || data.isInWater || data.isInCobweb || (data.lastStairsOrSlab == now && now - lastStairsOrSlab < 1000L)) {
                data.fallDistance = 0.0F; // Reset
            }
            if (data.onGround) {
                NO_FALL_CHECK.check(player, acPlayer, data);
            } else if (from.getY() > to.getY()) {
                data.fallDistance += from.getY() - to.getY();
            }
            // Run moving checks (async)
            if (!data.justTeleported) {
                new HandleMoveAsync(player, acPlayer, data);
            } else {
                data.justTeleported = false;
            }
        }
    }
}
