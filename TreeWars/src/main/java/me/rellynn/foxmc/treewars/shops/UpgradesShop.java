package me.rellynn.foxmc.treewars.shops;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.shops.items.ShopUpgrade;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class UpgradesShop extends VirtualMenu {

    public UpgradesShop() {
        super("upgrades", 3, "Upgrades Shop");
        addItemToDisplay(11, new ShopUpgrade(Upgrade.MINING_SPEED));
        addItemToDisplay(12, new ShopUpgrade(Upgrade.TRAP));
        addItemToDisplay(13, new ShopUpgrade(Upgrade.MINING_FATIGUE));
        addItemToDisplay(14, new ShopUpgrade(Upgrade.FAST_RESPAWN));
    }
}
