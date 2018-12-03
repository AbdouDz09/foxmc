package me.rellynn.foxmc.treewars.shops.submenus;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.special.ArmorPart;
import org.bukkit.Material;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class ArmourMenu extends SubShopMenu {

    public ArmourMenu() {
        super("armour", 1, "Armour");
        addItemToDisplay(1, new ArmorPart("leather_chestplate", TWCurrency.SEEDS, 10, Material.LEATHER_CHESTPLATE, "§cLeather Chestplate"));
        addItemToDisplay(2, new ArmorPart("chain_chestplate", TWCurrency.SEEDS, 30, Material.CHAINMAIL_CHESTPLATE, "§cChain Chestplate"));
        addItemToDisplay(3, new ArmorPart("iron_chestplate", TWCurrency.COCOA, 15, Material.IRON_CHESTPLATE, "§cIron Chestplate"));
        addItemToDisplay(4, new ArmorPart("diamond_chestplate", TWCurrency.DEAD_BUSH, 8, Material.DIAMOND_CHESTPLATE, "§cDiamond Chestplate"));
    }
}
