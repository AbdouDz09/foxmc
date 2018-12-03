package me.rellynn.foxmc.hub.hotbar;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;


/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class CustomHotbar extends VirtualMenu {

    public CustomHotbar() {
        super("hotbar", "Hotbar");
        addItemToDisplay(0, HubItems.mainItem);
        addItemToDisplay(1, HubItems.shopsItem);
        addItemToDisplay(2, HubItems.visibilityItem);
        addItemToDisplay(7, HubItems.featuresItem);
        addItemToDisplay(8, HubItems.selectorItem);
    }
}
