package me.rellynn.foxmc.hub.main.game;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
public class GameQueueItem extends VirtualItem {
    private String queueTitle;
    private MatchesMenu matchesMenu;
    private Set<String> filters = new HashSet<>();

    private ItemStack item;

    GameQueueItem(GameMenu menu, String mode, String queueTitle) {
        super(menu.getGameType() + "_" + mode);
        this.queueTitle = queueTitle;
        this.matchesMenu = new MatchesMenu(menu, mode, queueTitle);
        filters.add("game:" + menu.getGameType());
        filters.add("mode:" + mode);
        refreshItem();
    }

    void refreshItem() {
        int players = FoxAPI.get().getMatchesHandler().filterMatches(filters).stream().map(MatchData::getPlayers).reduce(0, Integer::sum);
        this.item = new ItemBuilder(Material.PAPER)
                .setTitle("§b" + queueTitle)
                .addLore("")
                .addLore("§e> §aLeft click §7Join queue")
                .addLore("§e> §aRight click §7Show servers")
                .addLore("")
                .addLore("§e" + players + " §7player" + (players != 1 ? "s" : "") + " in game")
                .build();
    }

    @Override
    protected void onClick(Player player, PerformedAction action) {
        if (action == PerformedAction.INVENTORY_RIGHT) {
            matchesMenu.open(player);
        }
    }

    @Override
    public ItemStack getItem(Player player) {
        return item;
    }
}
