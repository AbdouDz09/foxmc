package me.rellynn.foxmc.hub.games.ffa.shop.aura;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public class AuraShopMenu extends VirtualMenu {

    public AuraShopMenu() {
        super("ffa_aura", "Aura FFA");
        addItemToDisplay(getSlot(3, 1), new EndAuraBuyable());
        addItemToDisplay(getSlot(4, 1), new FlameAuraBuyable());
        addItemToDisplay(getSlot(5, 1), new CloudAuraBuyable());
        addItemToDisplay(getSlot(6, 1), new WaterAuraBuyable());
        addItemToDisplay(getSlot(7, 1), new GlyphAuraBuyable());
        addItemToDisplay(getSlot(1, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 2), HubItems.backMenuItem);
    }
}
