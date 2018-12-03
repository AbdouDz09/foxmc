package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class ArcheryMenu extends SubShopMenu {

    public ArcheryMenu() {
        super("archery", 1, "Archery");
        addItemToDisplay(1, new ShopItem("bow", TWCurrency.DEAD_BUSH, 5, new ItemStack(Material.BOW), "§cBow"));
        addItemToDisplay(2, new ShopItem("arrows", TWCurrency.COCOA, 2, new ItemStack(Material.ARROW, 5), "§cArrow x5"));
    }
}
