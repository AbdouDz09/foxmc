package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class FoodMenu extends SubShopMenu {

    public FoodMenu() {
        super("food", 1, "Food");
        addItemToDisplay(1, new ShopItem("carrot", TWCurrency.SEEDS, 1, new ItemStack(Material.CARROT_ITEM), "§cCarrot"));
        addItemToDisplay(2, new ShopItem("cooked_beef", TWCurrency.SEEDS, 3, new ItemStack(Material.COOKED_BEEF), "§cCooked Beef"));
        addItemToDisplay(3, new ShopItem("cake", TWCurrency.SEEDS, 12, new ItemStack(Material.CAKE), "§cCake"));
    }
}
