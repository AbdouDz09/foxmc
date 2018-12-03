package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import me.rellynn.foxmc.treewars.shops.items.special.ColoredBlockItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class BlocksMenu extends SubShopMenu {

    public BlocksMenu() {
        super("blocks", 1, "Blocks");
        addItemToDisplay(1, new ColoredBlockItem("glass", TWCurrency.SEEDS, 4, new ItemStack(Material.STAINED_GLASS, 16), "§cGlass x16"));
        addItemToDisplay(2, new ColoredBlockItem("clay", TWCurrency.SEEDS, 10, new ItemStack(Material.STAINED_CLAY, 16), "§cClay x16"));
        addItemToDisplay(3, new ShopItem("endstone", TWCurrency.SEEDS, 25, new ItemStack(Material.ENDER_STONE, 16), "§cEnd Stone x16"));
        addItemToDisplay(4, new ShopItem("wood_planks", TWCurrency.COCOA, 5, new ItemStack(Material.WOOD, 16), "§cWood Planks x16"));
        addItemToDisplay(5, new ShopItem("ladder", TWCurrency.SEEDS, 10, new ItemStack(Material.LADDER, 16), "§cLadder x16"));
        addItemToDisplay(6, new ShopItem("obsidian", TWCurrency.DEAD_BUSH, 4, new ItemStack(Material.OBSIDIAN, 4), "§cObsidian x4"));
    }
}
