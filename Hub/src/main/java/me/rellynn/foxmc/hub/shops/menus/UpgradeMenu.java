package me.rellynn.foxmc.hub.shops.menus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableUpgradable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class UpgradeMenu extends VirtualMenu {

    public UpgradeMenu(ItemBuyableUpgradable item, LevelInfoBuyable levelInfo) {
        super(item.getId() + "_upgrades", "Item upgrades");
        addItemToDisplay(getSlot(1, 2), new UpgradeLevel(id + "_0", levelInfo));
        Map<Integer, LevelInfoBuyable> upgrades = new HashMap<>(item.getLevels());
        upgrades.remove(upgrades.size() - 1);
        upgrades.forEach((key, value) -> addItemToDisplay(getSlot(3 + key, 2), new UpgradeLevel(id + "_" + (key + 1), value)));
        addItemToDisplay(getSlot(1, 3), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 3), HubItems.backMenuItem);
    }

    private class UpgradeLevel extends VirtualItem {
        private ItemStack item;

        UpgradeLevel(String id, LevelInfoBuyable levelInfo) {
            super(id, VirtualItem.ActionType.NOTHING);
            item = new ItemBuilder(levelInfo.getIcon())
                    .setTitle(levelInfo.getTitle())
                    .addLore(levelInfo.getLore())
                    .build();
        }

        @Override
        public ItemStack getItem(Player player) {
            return item;
        }
    }
}
