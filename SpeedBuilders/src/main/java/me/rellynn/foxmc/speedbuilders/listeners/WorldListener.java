package me.rellynn.foxmc.speedbuilders.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public class WorldListener implements Listener {

    /*
    Players
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent evt) {
        evt.setCancelled(true);
    }

    /*
    Blocks
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent evt) {
        evt.setNewCurrent(0);
    }

    /*
    World
     */
    @EventHandler
    public void onStructureGrow(StructureGrowEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent evt) {
        if (evt.toWeatherState())
            evt.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent evt) {
        if (evt.toThunderState())
            evt.setCancelled(true);
    }

    /*
    Entities
     */
    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent evt) {
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
    public void onEntityCombust(EntityCombustEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent evt) {
        if (evt.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            evt.setCancelled(true);
    }
}
