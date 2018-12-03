package me.rellynn.foxmc.hub.main;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.main.game.GameMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
public class GameIconItem extends VirtualItem {
    private GameMenu menu;
    private ItemStack icon;
    private List<String> description;

    private boolean arrow;
    private ItemStack item;

    GameIconItem(GameMenu menu, ItemStack icon) {
        super(menu.getGameType() + "_icon", ActionType.OPEN_MENU, menu.getId());
        this.menu = menu;
        this.icon = icon;
        this.description = HubPlugin.get().getConfig().getStringList("games." + menu.getGameType());
        refreshItem();
    }

    void refreshItem() {
        this.arrow = !arrow;
        int players = FoxAPI.get().getMatchesHandler().filterMatches("game:" + menu.getGameType()).stream().map(MatchData::getPlayers).reduce(0, Integer::sum);
        this.item = new ItemBuilder(icon)
                .setTitle("§6" + menu.getTitle())
                .addLore(description)
                .addLore("")
                .addLore("§e" + (arrow ? "► " : "  ") + players + " player" + (players != 1 ? "s" : "") + " in game")
                .setAttributes(false)
                .build();
    }

    @Override
    public ItemStack getItem(Player player) {
        return item;
    }
}
