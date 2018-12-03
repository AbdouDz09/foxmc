package me.rellynn.foxmc.games.listeners;

import me.rellynn.foxmc.game.api.GameAPI;
import me.rellynn.foxmc.game.api.GameState;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by gwennaelguich on 14/05/2017.
 * FoxMC Network.
 */
public class GameWorldListener implements Listener {
    public static final String STORE_META = "GAME_FROM";

    private DuelsPlugin plugin = DuelsPlugin.get();

    /*
    Cancel some world/entity events
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
    public void onEntityExplode(EntityExplodeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent evt) {
        evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        if (evt.getState() == PlayerFishEvent.State.CAUGHT_FISH)
            evt.setCancelled(true);
    }

    /*
    Entities
     */
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent evt) {
        evt.getVehicle().remove();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent evt) {
        if (!(evt.getEntity() instanceof FishHook))
            evt.getEntity().remove();
    }

    /*
    Explosions
     */
    @EventHandler
    public void onExpBottle(ExpBottleEvent evt) {
        evt.setExperience(0);
        evt.setShowEffect(false);
    }

    /*
    Entities
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
        evt.setDroppedExp(0);
        evt.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent evt) {
        if (evt.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        Game game = GameAPI.get().getGame(player);
        if (game != null && evt.hasBlock() && evt.hasItem() && evt.getItem().getData() instanceof SpawnEgg) {
            evt.setCancelled(true);
            SpawnEgg egg = (SpawnEgg) evt.getItem().getData();
            if (egg.getSpawnedType() != EntityType.HORSE) {
                player.sendMessage("§cYou can only spawn horses!");
                return;
            }
            ItemStack item = evt.getItem();
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItemInHand(item.getAmount() == 0 ? null : item);
            player.updateInventory();
            Horse horse = (Horse) player.getWorld().spawnEntity(evt.getClickedBlock().getRelative(evt.getBlockFace()).getLocation(), egg.getSpawnedType());
            horse.setMetadata(STORE_META, new FixedMetadataValue(plugin, game));
            horse.setTamed(true);
            horse.setOwner(player);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.setPassenger(player);
        }
    }

    /*
    Blocks
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockMultiPlace(BlockMultiPlaceEvent evt) {
        Game game = GameAPI.get().getGame(evt.getPlayer());
        if (game == null || !game.isState(GameState.STARTED)) {
            evt.setCancelled(true);
        } else {
            // Multi block example: a bed
            evt.getReplacedBlockStates().forEach(state -> plugin.getDuel(game).addBlock(state));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        Game game = GameAPI.get().getGame(evt.getPlayer());
        if (game == null || !game.isState(GameState.STARTED) || evt.getItemInHand().getType() == Material.SPONGE) {
            evt.setCancelled(true);
        } else {
            plugin.getDuel(game).addBlock(evt.getBlockReplacedState());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent evt) {
        evt.setCancelled(true);
        Game game = GameAPI.get().getGame(evt.getPlayer());
        if (game != null && game.isState(GameState.STARTED) && plugin.getDuel(game).removeBlock(evt.getBlock()))
            evt.getBlock().setType(Material.AIR);
    }

    /*
    Water & lava
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent evt) {
        Game game = GameAPI.get().getGame(evt.getPlayer());
        if (game == null || !game.isState(GameState.STARTED) || !plugin.getDuel(game).removeBlock(evt.getBlockClicked()))
            evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent evt) {
        Game game = GameAPI.get().getGame(evt.getPlayer());
        Block block = evt.getBlockClicked().getRelative(evt.getBlockFace());
        if (game == null || !game.isState(GameState.STARTED) || block.getType() != Material.AIR) {
            evt.setCancelled(true);
        } else {
            plugin.getDuel(game).addBlock(block);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent evt) {
        Block toBlock = evt.getToBlock();
        if (toBlock.getType() != Material.AIR || !evt.getBlock().hasMetadata(STORE_META)) {
            evt.setCancelled(true);
        } else {
            Game game = (Game) evt.getBlock().getMetadata(STORE_META).get(0).value();
            plugin.getDuel(game).addBlock(toBlock);
        }
    }

    /*
    Fire
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent evt) {
        if (!evt.getBlock().hasMetadata(STORE_META)) {
            evt.setCancelled(true);
        } else {
            Game game = (Game) evt.getBlock().getMetadata(STORE_META).get(0).value();
            plugin.getDuel(game).removeBlock(evt.getBlock());
            evt.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent evt) {
        if (evt.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            evt.setCancelled(true);
            return;
        }
        Game game = GameAPI.get().getGame(evt.getPlayer());
        if (game == null || !game.isState(GameState.STARTED)) {
            evt.setCancelled(true);
        } else {
            plugin.getDuel(game).addBlock(evt.getBlock());
        }
    }

    /*
    Falling blocks
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        if (evt.getEntity() instanceof FallingBlock) {
            boolean entityHasMeta = evt.getEntity().hasMetadata(STORE_META);
            if (!entityHasMeta && evt.getBlock().hasMetadata(STORE_META)) {
                evt.getEntity().setMetadata(STORE_META, evt.getBlock().getMetadata(STORE_META).get(0));
            } else if (evt.getTo() == ((FallingBlock) evt.getEntity()).getMaterial() && entityHasMeta) {
                Game game = (Game) evt.getEntity().getMetadata(STORE_META).get(0).value();
                plugin.getDuel(game).addBlock(evt.getBlock());
            }
        }
    }
}
