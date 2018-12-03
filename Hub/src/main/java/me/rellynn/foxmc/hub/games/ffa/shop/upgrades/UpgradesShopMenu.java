package me.rellynn.foxmc.hub.games.ffa.shop.upgrades;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class UpgradesShopMenu extends VirtualMenu {

    public UpgradesShopMenu() {
        super("ffa_upgrades", "Upgrades FFA");
        addItemToDisplay(getSlot(5, 1), new HealthRegenUpgrade());
        addItemToDisplay(getSlot(1, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 2), HubItems.backMenuItem);
    }
}
