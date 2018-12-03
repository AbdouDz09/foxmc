package me.rellynn.foxmc.speedbuilders.listeners;

import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.phases.BuildingPhase;
import me.rellynn.foxmc.speedbuilders.phases.ShowBuildPhase;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import me.rellynn.foxmc.speedbuilders.utils.SBUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by gwennaelguich on 05/07/2017.
 * FoxMC Network.
 */
public class BlockListener implements Listener {
    private SBGame game = SBPlugin.getGame();

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent evt) {
        if (game.isPhase(ShowBuildPhase.class) || evt.getBlock().hasMetadata("INSTANT_BREAK"))
            evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent evt) {
        Player player = evt.getPlayer();
        if (!game.isPhase(BuildingPhase.class)) {
            evt.setCancelled(true);
            player.sendMessage("§cThe game isn't in build phase!");
        } else {
            Block block = evt.getBlock();
            Platform<Player> platform = game.getPlatform(player);
            Location location = block.getLocation();
            if (platform == null || !platform.canBuild(location)) {
                evt.setCancelled(true);
                player.sendMessage("§cYou aren't allowed to place blocks here!");
            } else {
                List<BlockState> states = (evt instanceof BlockMultiPlaceEvent ? ((BlockMultiPlaceEvent) evt).getReplacedBlockStates() : Arrays.asList(block.getState()));
                states.forEach(state -> state.getBlock().setMetadata("INSTANT_BREAK", new FixedMetadataValue(SBPlugin.get(), states)));
                BlockState replacedState = evt.getBlockReplacedState();
                if (replacedState.getType() != Material.AIR && block.getType() == evt.getItemInHand().getType()) {
                    Set<ItemStack> items = SBUtils.getBuildItems(replacedState.getType(), replacedState.getRawData());
                    items.forEach(item -> player.getInventory().addItem(item));
                }
            }
        }
    }
}
