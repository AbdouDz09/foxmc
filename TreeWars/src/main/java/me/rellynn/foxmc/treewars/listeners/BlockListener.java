package me.rellynn.foxmc.treewars.listeners;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.utils.BroadcastType;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.game.TWGame;
import me.rellynn.foxmc.treewars.game.Tree;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class BlockListener implements Listener {
    private TWGame game = TWPlugin.getGame();

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent evt) {
        if (evt.getBlock().getType() == Material.SAPLING || game.getLobby().contains(evt.getBlock()))
            evt.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent evt) {
        Player player = evt.getPlayer();
        // Check for currencies
        if (Arrays.stream(TWCurrency.values()).anyMatch(currency -> currency.getItem().getType() == evt.getBlock().getType())) {
            evt.setCancelled(true);
            player.sendMessage("§cYou can't place this block!");
            return;
        }
        // Spawns + Generators
        Location location = evt.getBlock().getLocation();
        if (game.getArena().getTeams().stream().anyMatch(team -> team.getPoint("spawn").distanceSquared(location) <= 25.0D ||
                game.getGenerators().stream().anyMatch(generator -> generator.getLocation().distanceSquared(location) <= 9.0D))) {
            evt.setCancelled(true);
            player.sendMessage("§cYou can't place blocks near spawns and generators!");
            return;
        }
        game.increaseStatistic(player, "blocks_placed");
        if (evt.getBlock().getType() != Material.TNT) {
            evt.getBlock().setMetadata("PLACED_BLOCKED", new FixedMetadataValue(TWPlugin.get(), true));
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    evt.getBlock().setType(Material.AIR);
                    TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
                    Reflection.getField(EntityTNTPrimed.class, "source", EntityLiving.class).set(((CraftTNTPrimed) tnt).getHandle(), ((CraftPlayer) player).getHandle());
                    tnt.setFuseTicks(35);
                }
            }.runTask(TWPlugin.get());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent evt) {
        Player player = evt.getPlayer();
        Block block = evt.getBlock();
        Location location = block.getLocation();
        if (block.hasMetadata("PLACED_BLOCK")) {
            Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 3.0D, 1.5D, 3.0D);
            if (entities.stream().noneMatch(entity -> entity instanceof Player && entity != player && location.equals(entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation()) && game.getArena().getPlayerTeam(player) == game.getArena().getPlayerTeam((Player) entity))) {
                block.removeMetadata("PLACED_BLOCK", TWPlugin.get());
                game.increaseStatistic(player, "blocks_broken");
            } else {
                evt.setCancelled(true);
                player.sendMessage("§cYou can't break a block under your teammate!");
            }
            return;
        }
        evt.setCancelled(true);
        if (block.getType() != Material.SAPLING) {
            player.sendMessage("§cYou can only break blocks placed by players!");
            return;
        }
        Tree tree = game.getTrees().values().stream().filter(t -> t.isThis(block)).findFirst().orElse(null);
        if (tree == null) {
            player.sendMessage("§cThis isn't a tree!");
            return;
        }
        Team team = tree.getTeam();
        if (team.hasPlayer(player)) {
            player.playSound(player.getEyeLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            player.sendMessage("§cYou can't destroy your own tree!");
            return;
        }
        tree.destroy();
        team.getOnlinePlayers(game).forEach(teamPlayer -> {
            PacketUtils.sendTitle("§cTREE DESTROYED!", teamPlayer);
            PacketUtils.sendSubTitle("You will no longer respawn!", teamPlayer);
        });
        game.increaseStatistic(player, "trees_destroyed");
        game.giveCoins(player, 7.0F, "tree destroyed");
        game.giveTails(player, 3.0F, "tree destroyed");
        game.broadcast(game.getDisplayName(player) + " §7destroyed the " + team.getDisplayName() + " Tree§7!");
        game.broadcast(BroadcastType.SOUND, Sound.ENDERDRAGON_GROWL);
    }
}
