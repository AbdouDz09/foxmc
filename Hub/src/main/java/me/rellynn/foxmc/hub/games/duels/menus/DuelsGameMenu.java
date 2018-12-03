package me.rellynn.foxmc.hub.games.duels.menus;

import me.rellynn.foxmc.hub.main.game.GameMenu;
import me.rellynn.foxmc.hub.main.game.GameShopItem;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
public class DuelsGameMenu extends GameMenu {

    public DuelsGameMenu() {
        super("duels", "Duels");
        addQueueItem("1vs1", "§b1 VS 1");
        addItemToDisplay(getSlot(9, 1), new GameShopItem("duels_shop"));
    }
}
