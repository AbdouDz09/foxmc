package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class ToolsMenu extends SubShopMenu {

    public ToolsMenu() {
        super("tools", 1, "Tools");
        addItemToDisplay(1, new ShopItem("stone_pickaxe", TWCurrency.SEEDS, 10, new ItemBuilder(Material.STONE_PICKAXE).build(), "§cStone Pickaxe"));
        addItemToDisplay(2, new ShopItem("iron_pickaxe", TWCurrency.COCOA, 8, new ItemBuilder(Material.IRON_PICKAXE).build(), "§cIron Pickaxe"));
        addItemToDisplay(3, new ShopItem("diamond_pickaxe_3", TWCurrency.COCOA, 15, new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchantment(Enchantment.DIG_SPEED, 3).build(), "§cDiamond Pickaxe"));
        addItemToDisplay(4, new ShopItem("iron_axe", TWCurrency.COCOA, 8, new ItemBuilder(Material.IRON_AXE).addEnchantment(Enchantment.DIG_SPEED, 2).build(), "§cIron Axe"));
    }
}
