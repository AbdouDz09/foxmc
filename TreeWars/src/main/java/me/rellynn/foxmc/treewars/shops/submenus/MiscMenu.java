package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 03/06/2017.
 * FoxMC Network.
 */
public class MiscMenu extends SubShopMenu {

    public MiscMenu() {
        super("misc", 1, "Miscellaneous");
        addItemToDisplay(1, new ShopItem("golden_apple", TWCurrency.COCOA, 3, new ItemStack(Material.GOLDEN_APPLE), "§cGolden Apple"));
        addItemToDisplay(2, new ShopItem("tnt", TWCurrency.COCOA, 5, new ItemStack(Material.TNT), "§cTNT x1"));
        addItemToDisplay(3, new ShopItem("ender_pearl", TWCurrency.DEAD_BUSH, 4, new ItemStack(Material.ENDER_PEARL), "§cEnder Pearl x1"));
        addItemToDisplay(4, new ShopItem("snowball", TWCurrency.SEEDS, 64, new ItemStack(Material.SNOW_BALL, 8), "§cSnowball x8"));
    }
}
