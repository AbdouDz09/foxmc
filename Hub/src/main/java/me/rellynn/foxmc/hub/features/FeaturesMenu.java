package me.rellynn.foxmc.hub.features;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

/**
 * Created by gwennaelguich on 22/05/2017.
 * FoxMC Network.
 */
public class FeaturesMenu extends VirtualMenu {

    public FeaturesMenu() {
        super("features", "Features");
        addItemToDisplay(getSlot(5, 1), HubItems.gadgetsItem);
        addItemToDisplay(getSlot(4, 2), HubItems.petsItem);
        addItemToDisplay(getSlot(5, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(6, 2), HubItems.mountsItem);
        addItemToDisplay(getSlot(5, 3), HubItems.effectsItem);
        addItemToDisplay(getSlot(1, 4), HubItems.settingsItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
    }
}
