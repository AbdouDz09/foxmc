package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class WeaponsMenu extends SubShopMenu {

    public WeaponsMenu() {
        super("weapons", 1, "Weapons");
        addItemToDisplay(1, new ShopItem("stone_sword", TWCurrency.SEEDS, 10, new ItemStack(Material.STONE_SWORD), "§cStone Sword"));
        addItemToDisplay(2, new ShopItem("iron_sword", TWCurrency.COCOA, 8, new ItemStack(Material.IRON_SWORD), "§cIron Sword"));
        addItemToDisplay(3, new ShopItem("diamond_sword", TWCurrency.DEAD_BUSH, 4, new ItemStack(Material.DIAMOND_SWORD), "§cDiamond Sword"));
        addItemToDisplay(4, new ShopItem("feather_knockback", TWCurrency.COCOA, 15, new ItemBuilder(Material.FEATHER).addEnchantment(Enchantment.KNOCKBACK, 2).build(), "§cFeather Knockback II"));
    }
}
