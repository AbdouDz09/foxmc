package me.rellynn.foxmc.hub.main;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.games.duels.menus.DuelsGameMenu;
import me.rellynn.foxmc.hub.games.ffa.menus.FFAGameMenu;
import me.rellynn.foxmc.hub.games.sbuilders.SBuildersGameMenu;
import me.rellynn.foxmc.hub.games.twars.TWarsGameMenu;
import me.rellynn.foxmc.hub.main.game.GameMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class MainMenu extends VirtualMenu {
    private Set<GameIconItem> items = new HashSet<>();

    public MainMenu() {
        super("main", "Main Menu", 20L);
        addItemToDisplay(getSlot(9, 1), HubItems.shopsItem);
        addItemToDisplay(getSlot(2, 2), HubItems.spawnItem);
        addItemToDisplay(getSlot(5, 4), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
        addGameToDisplay(getSlot(4, 2), new DuelsGameMenu(), new ItemStack(Material.DIAMOND_SWORD));
        addGameToDisplay(getSlot(5, 2), new FFAGameMenu(), new ItemStack(Material.FISHING_ROD));
        addGameToDisplay(getSlot(6, 2), new TWarsGameMenu(), new ItemStack(Material.DEAD_BUSH));
        addGameToDisplay(getSlot(7, 2), new SBuildersGameMenu(), new ItemStack(Material.QUARTZ_BLOCK));
    }

    private void addGameToDisplay(int slot, GameMenu menu, ItemStack icon) {
        GameIconItem item = new GameIconItem(menu, icon);
        addItemToDisplay(slot, item);
        items.add(item);
    }

    @Override
    protected void onUpdate() {
        items.forEach(GameIconItem::refreshItem);
    }
}
