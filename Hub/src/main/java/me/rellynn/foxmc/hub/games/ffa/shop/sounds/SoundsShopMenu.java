package me.rellynn.foxmc.hub.games.ffa.shop.sounds;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class SoundsShopMenu extends VirtualMenu {

    public SoundsShopMenu() {
        super("ffa_sounds", "Sounds FFA");
        addItemToDisplay(getSlot(2, 1), new DonkeySoundBuyable());
        addItemToDisplay(getSlot(3, 1), new HorseSoundBuyable());
        addItemToDisplay(getSlot(4, 1), new CatSoundBuyable());
        addItemToDisplay(getSlot(5, 1), new WolfSoundBuyable());
        addItemToDisplay(getSlot(6, 1), new CowSoundBuyable());
        addItemToDisplay(getSlot(7, 1), new SheepSoundBuyable());
        addItemToDisplay(getSlot(8, 1), new GhastSoundBuyable());
        addItemToDisplay(getSlot(1, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 2), HubItems.backMenuItem);
    }
}
