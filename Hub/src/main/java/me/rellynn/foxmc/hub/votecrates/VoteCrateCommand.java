package me.rellynn.foxmc.hub.votecrates;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.hub.api.HubAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class VoteCrateCommand extends PlayerCommand {

    VoteCrateCommand() {
        super(Rank.OWNER);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        Block target = player.getTargetBlock((Set<Material>) null, 5);
        if (target == null || target.getType() == Material.AIR) {
            player.sendMessage("§cYou must target a block!");
            return;
        }
        HubAPI.get().getVoteCratesManager().setVoteCrate(target.getLocation());
        player.sendMessage("§aVote crate created!");
    }
}
