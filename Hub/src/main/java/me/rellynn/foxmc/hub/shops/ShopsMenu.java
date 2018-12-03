package me.rellynn.foxmc.hub.shops;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.games.duels.shop.DuelsShopMenu;
import me.rellynn.foxmc.hub.games.ffa.shop.FFAShopMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class ShopsMenu extends VirtualMenu {

    public ShopsMenu() {
        super("shops", "Shops");
        addItemToDisplay(getSlot(4, 2), new VirtualItem("duels_shop", VirtualItem.ActionType.OPEN_MENU, new DuelsShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.DIAMOND_SWORD)
                    .setTitle("§e1 VS 1")
                    .setAttributes(false)
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(6, 2), new VirtualItem("ffa_shop", VirtualItem.ActionType.OPEN_MENU, new FFAShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.FISHING_ROD)
                    .setTitle("§eFFA")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(9, 3), HubItems.backMenuItem);
    }
}
