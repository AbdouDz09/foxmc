package me.rellynn.foxmc.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class WorldListener implements Listener {

    /*
    World
     */
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent evt) {
        if (evt.toWeatherState()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent evt) {
        if (evt.toThunderState()) {
            evt.setCancelled(true);
        }
    }

    /*
    Player
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
        }
    }

    /*
    Entities
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent evt) {
        if (evt.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            evt.setCancelled(true);
        }
    }
}
