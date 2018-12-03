package me.rellynn.foxmc.ffa.commands;

import com.google.gson.JsonObject;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.ffa.FFAPlugin;
import me.rellynn.foxmc.gameapiv2.commands.GameCommand;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 25/05/2017.
 * FoxMC Network.
 */
public class SaveCommand extends GameCommand {

    @Override
    public void onCommand(Game game, Player player, String[] args) {
        PlayerData data = FoxAPI.getPlayer(player);
        if (!data.isAtLeast(Rank.VIP)) {
            player.sendMessage("§cYou must be " + Rank.VIP.getName() + " §cor higher to save your kit!");
            return;
        } else if (!FFAPlugin.getGame().getSpawn().contains(player.getLocation())) {
            player.sendMessage("§cYou have to be in the safe zone to save your kit!");
            return;
        }
        JsonObject slots = new JsonObject();
        for (ItemStack item : FFAPlugin.getGame().getItems()) {
            if (item != null && item.getType() != Material.MUSHROOM_SOUP) {
                int slot = player.getInventory().first(item);
                if (slot == -1) {
                    player.sendMessage("§cSorry but you can't save because an item is missing from your inventory!");
                    return;
                }
                slots.addProperty(item.getType().name() + ":" + item.getDurability(), slot);
            }
        }
        data.setCustomData("ffa." + FFAPlugin.getGame().getMode().getId() + ".slots", slots);
        player.sendMessage("§aKit saved!");
    }
}
