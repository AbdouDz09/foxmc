package me.rellynn.foxmc.speedbuilders.listeners;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerLeaveGameEvent;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerSpectateGameEvent;
import me.rellynn.foxmc.gameapiv2.utils.EventUtils;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.phases.BuildingPhase;
import me.rellynn.foxmc.speedbuilders.phases.JudgeBuildsPhase;
import me.rellynn.foxmc.speedbuilders.phases.StartPhase;
import me.rellynn.foxmc.speedbuilders.scheduler.CheckPerfectBuild;
import me.rellynn.foxmc.speedbuilders.scheduler.SpectatorTracker;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import me.rellynn.foxmc.speedbuilders.utils.SBUtils;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import java.util.List;
import java.util.Set;

/**
 * Created by gwennaelguich on 02/07/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private SBGame game = SBPlugin.getGame();

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent evt) {
        if (game.isState(GameState.RUNNING)) {
            Platform<Player> platform = game.getPlatform(evt.getPlayer());
            if (!game.isPhase(JudgeBuildsPhase.class) || platform != ((JudgeBuildsPhase) game.getCurrentPhase()).getLooserPlatform()) {
                game.removePlatform(platform);
                game.checkWin();
            }
        }
    }

    @EventHandler
    public void onPlayerSpectateGame(PlayerSpectateGameEvent evt) {
        Player spectator = evt.getPlayer();
        if (game.isState(GameState.RUNNING)) {
            game.createScoreboard(spectator);
        }
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(spectator.getEntityId());
        Packet packet = game.getMorphPacket(spectator);
        game.getSpectators().forEach(player -> {
            if (player != spectator) {
                PacketUtils.sendPacket(player, destroyPacket);
                PacketUtils.sendPacket(player, packet);
            }
        });
        new SpectatorTracker(spectator);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        if (game.isPhase(StartPhase.class) && !game.isSpectator(player) && (evt.getFrom().getX() != evt.getTo().getX() || evt.getFrom().getZ() != evt.getTo().getZ())) {
            evt.setTo(evt.getFrom());
        } else if (EventUtils.hasMoved(evt) && game.isPlayer(player)) {
            if (game.isState(GameState.WAITING) && evt.getTo().getBlock().isLiquid()) {
                evt.setTo(game.getArena().getPoint("lobby"));
                player.playSound(player.getEyeLocation(), Sound.SPLASH, 1.0F, 1.0F);
                PacketUtils.sendTitle("", player);
                PacketUtils.sendSubTitle("§cYou can't leave the waiting room!", player);
            } else if (!game.isState(GameState.ENDING) && !game.isPhase(JudgeBuildsPhase.class)) {
                Platform<Player> platform = game.getPlatform(player);
                if (platform != null && !platform.contains(evt.getTo())) {
                    evt.setTo(platform.getSpawn());
                    player.playSound(player.getEyeLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                    PacketUtils.sendTitle("", player);
                    PacketUtils.sendSubTitle("§cYou can't leave your platform!", player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        Block clickedBlock = evt.getClickedBlock();
        Platform<Player> platform;
        if (evt.getAction() == Action.PHYSICAL || !game.isPhase(BuildingPhase.class) || clickedBlock == null || (platform = game.getPlatform(clickedBlock.getLocation())) == null || !platform.isEntity(player) || !platform.isEditAllowed()) {
            evt.setCancelled(true);
        } else if (evt.getAction() == Action.LEFT_CLICK_BLOCK && clickedBlock.hasMetadata("INSTANT_BREAK")) {
            // Instant break
            ((List<BlockState>) clickedBlock.getMetadata("INSTANT_BREAK").get(0).value()).forEach(state -> {
                Block block = state.getBlock();
                Set<ItemStack> items = SBUtils.getBuildItems(block.getType(), block.getData());
                if (!items.isEmpty()) {
                    items.forEach(item -> player.getInventory().addItem(item));
                    player.updateInventory();
                }
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                block.setType(Material.AIR);
            });
        } else if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (clickedBlock.getType() == Material.LEVER && !player.isSneaking()) {
                Lever lever = (Lever) clickedBlock.getState().getData();
                lever.setPowered(!lever.isPowered());
                clickedBlock.setData(lever.getData(), false);
            } else if (evt.hasItem() && SBUtils.isSpawningItem(evt.getItem())) {
                evt.setCancelled(true);
                // Entities
                if (!platform.canBuild(clickedBlock.getLocation())) {
                    player.sendMessage("§cYou can't place an entity here!");
                    player.updateInventory();
                    return;
                }
                player.setItemInHand(null);
                player.updateInventory();
                SBUtils.spawnFromItem(clickedBlock.getRelative(evt.getBlockFace()).getLocation(), platform, evt.getItem());
            }
            new CheckPerfectBuild(platform);
        }
    }
}
