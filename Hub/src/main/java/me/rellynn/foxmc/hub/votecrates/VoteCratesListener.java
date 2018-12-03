package me.rellynn.foxmc.hub.votecrates;

import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class VoteCratesListener implements Listener {
    private VoteCratesManager manager;

    VoteCratesListener(VoteCratesManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        manager.showHolograms(evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (manager.removeCrate(evt.getBlock())) {
            evt.getPlayer().sendMessage("§aVote crate destroyed!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.hasBlock() && manager.isCrate(evt.getClickedBlock())) {
            evt.setCancelled(true);
            Player player = evt.getPlayer();
            if (FoxAPI.getPlayer(player).hasVotedToday()) {
                player.sendMessage("§cYou already voted today! Please come back again tomorrow to win more coins! :)");
            } else {
                player.sendMessage("§7Please vote on this website: §b§nhttp://minecraftservers.org/server/438604 §7to win §675 FoxCoins");
            }
        }
    }
}
