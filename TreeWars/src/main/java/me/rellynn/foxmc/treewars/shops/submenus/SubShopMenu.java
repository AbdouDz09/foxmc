package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class SubShopMenu extends VirtualMenu {

    protected SubShopMenu(String id, int size, String title) {
        super("subshop_" + id, size, title);
        addItemToDisplay(size * 9 + 4, new VirtualItem("shop_return_" + id, VirtualItem.ActionType.OPEN_MENU, "shop") {
            private final ItemStack ITEM = new ItemBuilder(Material.ARROW)
                    .setTitle("§cReturn to shop")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
    }
}
