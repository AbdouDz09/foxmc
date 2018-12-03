package me.rellynn.foxmc.hub.games.duels.shop;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.games.duels.shop.sounds.CatSound;
import me.rellynn.foxmc.hub.games.duels.shop.sounds.DonkeySound;

/**
 * Created by gwennaelguich on 23/06/2017.
 * FoxMC Network.
 */
public class DuelsShopMenu extends VirtualMenu {

    public DuelsShopMenu() {
        super("duels_shop", "1 VS 1 Killing Sounds");
        addItemToDisplay(getSlot(4, 1), new DonkeySound());
        addItemToDisplay(getSlot(6, 1), new CatSound());
        addItemToDisplay(getSlot(1, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 2), HubItems.backMenuItem);
    }
}
