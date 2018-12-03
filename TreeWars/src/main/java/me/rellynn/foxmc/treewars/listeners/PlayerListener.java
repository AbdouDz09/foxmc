package me.rellynn.foxmc.treewars.listeners;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerJoinGameEvent;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerLeaveGameEvent;
import me.rellynn.foxmc.gameapiv2.utils.EventUtils;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWGame;
import me.rellynn.foxmc.treewars.game.Tree;
import me.rellynn.foxmc.treewars.game.generators.Generator;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.MiningFatigueUpgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.TrapUpgrade;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private TWGame game = TWPlugin.getGame();

    /*
    Join/Leave messages
     */
    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent evt) {
        if (game.isState(GameState.RUNNING))
            game.broadcast(evt.getPlayer().getDisplayName() + " §7reconnected.");
    }

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent evt) {
        if (game.isState(GameState.RUNNING)) {
            game.broadcast(evt.getPlayer().getDisplayName() + " §7disconnected.");
            if (evt.getPlayer().hasMetadata("RESPAWNING"))
                game.respawn(evt.getPlayer());
        }
    }

    /*
    Prevent armor modifications & crafts
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getClickedInventory() == evt.getWhoClicked().getInventory()
                && (evt.getSlotType() == InventoryType.SlotType.ARMOR || evt.getSlotType() == InventoryType.SlotType.CRAFTING))
            evt.setCancelled(true);
    }

    /*
    Respawning players
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        if (evt.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && game.getArena().hasTeam(evt.getPlayer()))
            evt.setCancelled(true);
    }

    /*
    Generators upgrades
     */
    private void openUpgrades(Player player, Cancellable evt, Metadatable metadatable) {
        if (metadatable.hasMetadata("GENERATOR") && game.getArena().getPlayerTeam(player) != null) {
            evt.setCancelled(true);
            ((Generator) metadatable.getMetadata("GENERATOR").get(0).value()).openUpgrades(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent evt) {
        openUpgrades(evt.getPlayer(), evt, evt.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent evt) {
        openUpgrades(evt.getPlayer(), evt, evt.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.hasBlock())
            openUpgrades(evt.getPlayer(), evt, evt.getClickedBlock());
    }

    /*
    Shop & Upgrades villagers
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        if (evt.getRightClicked().hasMetadata("OPEN_MENU")) {
            evt.setCancelled(true);
            if (game.isState(GameState.RUNNING) && game.getArena().hasTeam(evt.getPlayer())) {
                VirtualMenu.getMenu(evt.getRightClicked().getMetadata("OPEN_MENU").get(0).asString()).open(evt.getPlayer());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        // Used to reset sword blocking / eating state
                        ((CraftPlayer) evt.getPlayer()).getHandle().bU();
                    }
                }.runTask(TWPlugin.get());
            }
        }
    }

    /*
    Balance drops
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        if (evt.getItem().hasMetadata("FROM_GENERATOR")) {
            return;
        }
        Player player = evt.getPlayer();
        Team team = game.getArena().getPlayerTeam(player);
        if (team != null && player.hasMetadata("LAST_GEN_PICKUP")) {
            long lastPickup = player.getMetadata("LAST_GEN_PICKUP").get(0).asLong();
            Location location = evt.getItem().getLocation();
            for (Entity entity : location.getWorld().getNearbyEntities(location, 1.3D, 0.5D, 1.3D)) {
                if (entity instanceof Player && entity != player && team.hasPlayer((Player) entity) && (!entity.hasMetadata("LAST_GEN_PICKUP") || lastPickup > entity.getMetadata("LAST_GEN_PICKUP").get(0).asLong())) {
                    evt.setCancelled(true);
                    return;
                }
            }
        }
        player.setMetadata("LAST_GEN_PICKUP", new FixedMetadataValue(TWPlugin.get(), System.currentTimeMillis()));
    }

    /*
    Team Inventory
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent evt) {
        if (evt.getInventory().getType() == InventoryType.ENDER_CHEST) {
            evt.setCancelled(true);
            Team team = game.getArena().getPlayerTeam((Player) evt.getPlayer());
            if (team != null) {
                // Open team inventory
                evt.getPlayer().openInventory(game.getTeamInventories().get(team));
            }
        }
    }

    /*
    Upgrades
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (EventUtils.hasMoved(evt) && game.isPlayer(evt.getPlayer())) {
            Player player = evt.getPlayer();
            Collection<Tree> trees = game.getTrees().values();
            Optional<Tree> optTree = trees.stream().filter(tree -> !tree.getTeam().hasPlayer(player) && tree.getLocation().distanceSquared(evt.getTo()) <= 40).findFirst();
            if (!optTree.isPresent()) {
                if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            } else {
                Tree tree = optTree.get();
                if (tree.hasUpgrade(Upgrade.MINING_FATIGUE)) {
                    player.addPotionEffect(MiningFatigueUpgrade.EFFECT);
                }
                if (tree.removeUpgrade(Upgrade.TRAP)) {
                    player.addPotionEffect(TrapUpgrade.BLINDNESS);
                    player.addPotionEffect(TrapUpgrade.SLOWNESS);
                    player.sendMessage("§cIt's a trap!");
                    PacketUtils.sendTitle("§cIt's a trap!", player);
                    game.increaseStatistic(player, "traps_triggered");
                    tree.getTeam().getOnlinePlayers(game).forEach(teamPlayer -> {
                        teamPlayer.sendMessage("§cSomeone entered your base!");
                        PacketUtils.sendTitle("§cTRAP TRIGGERED!", teamPlayer);
                        PacketUtils.sendSubTitle("Someone entered your base!", teamPlayer);
                    });
                }
            }
        }
    }
}
