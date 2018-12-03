package me.rellynn.foxmc.ffa.listeners;

import me.rellynn.foxmc.ffa.FFAGame;
import me.rellynn.foxmc.ffa.FFAPlugin;
import me.rellynn.foxmc.ffa.game.FFAMode;
import me.rellynn.foxmc.gameapiv2.utils.EventUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class WorldListener implements Listener {
    private static final List<Material> ALLOWED_MATERIALS = Arrays.asList(Material.FIRE, Material.WEB, Material.TNT);

    private FFAGame game = FFAPlugin.getGame();

    /*
    Players
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        if (evt.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage("§cYou almost caught a fish!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.getAction() == Action.PHYSICAL || (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getClickedBlock().getType().name().endsWith("CHEST"))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent evt) {
        if (evt.getItem().getType() != Material.GOLDEN_APPLE && evt.getItem().getType() != Material.POTION) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        Material type = evt.getItemDrop().getItemStack().getType();
        if (type != Material.BOWL && type != Material.GLASS_BOTTLE) {
            evt.setCancelled(true);
        } else {
            evt.getItemDrop().remove();
        }
    }

    /*
    Weather
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
    Entities
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        evt.blockList().clear();
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent evt) {
        if (evt.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN ||
                (evt.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && (game.isMode(FFAMode.UHC) || game.isMode(FFAMode.SOUP)))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent evt) {
        if (evt.getEntity() instanceof Arrow) {
            evt.getEntity().remove();
        }
    }

    /*
    Safe-Zone
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getCause() == EntityDamageEvent.DamageCause.FALL || (evt.getEntity() instanceof Player && game.getSpawn().contains(evt.getEntity().getLocation()))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        Player damager = EventUtils.getDamager(evt);
        if (damager != null && game.getSpawn().contains(damager.getLocation())) {
            evt.setCancelled(true);
            damager.sendMessage("§cYou are in the safe zone!");
        }
    }

    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent evt) {
        ProjectileSource shooter = evt.getEntity().getShooter();
        if (shooter instanceof Player && game.getSpawn().contains(((Player) shooter).getLocation())) {
            evt.setCancelled(true);
            ((Player) shooter).sendMessage("§cYou can't use this item in safe zone!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        if (EventUtils.hasMoved(evt) && !game.isSpectator(player) && !game.getSpawn().contains(evt.getFrom()) && game.getSpawn().contains(evt.getTo())) {
            evt.setCancelled(true);
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
            player.sendMessage("§cYou can't enter in the safe zone!");
            player.setVelocity(evt.getFrom().toVector().subtract(evt.getTo().toVector()).normalize().multiply(1.5D).setY(0.8D));
        }
    }

    /*
    Blocks
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!game.canBuild(evt.getPlayer())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent evt) {
        if (!game.canBuild(evt.getPlayer()) && (!ALLOWED_MATERIALS.contains(evt.getBlock().getType()) || game.getSpawn().contains(evt.getBlock().getLocation()))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent evt) {
        Player player = evt.getPlayer();
        if (player != null && evt.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && game.getSpawn().contains(evt.getBlock().getLocation())) {
            evt.setCancelled(true);
            player.sendMessage("§cYou can't use your flint and steel in safe zone!");
        }
    }
}
