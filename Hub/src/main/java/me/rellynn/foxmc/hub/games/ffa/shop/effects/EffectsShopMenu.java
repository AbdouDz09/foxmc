package me.rellynn.foxmc.hub.games.ffa.shop.effects;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public class EffectsShopMenu extends VirtualMenu {

    public EffectsShopMenu() {
        super("ffa_effects", "Effects FFA");
        addItemToDisplay(getSlot(4, 1), new CupidEffectBuyable());
        addItemToDisplay(getSlot(6, 1), new MagicalRodEffectBuyable());
        addItemToDisplay(getSlot(1, 2), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 2), HubItems.backMenuItem);
    }
}
