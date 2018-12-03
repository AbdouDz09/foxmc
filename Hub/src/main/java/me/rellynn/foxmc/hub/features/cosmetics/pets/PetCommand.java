package me.rellynn.foxmc.hub.features.cosmetics.pets;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 25/05/2017.
 * FoxMC Network.
 */
public class PetCommand extends PlayerCommand {

    PetCommand(Rank rank) {
        super(rank);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        if (args.length != 0 && args[0].equalsIgnoreCase("rename")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: §e/pet rename <name>");
                return;
            }
            String name = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), "_");
            // TODO: Finish pet rename
        }
    }
}
