package me.rellynn.foxmc.hub.games.ffa.menus;

import me.rellynn.foxmc.hub.main.game.GameMenu;
import me.rellynn.foxmc.hub.main.game.GameShopItem;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
public class FFAGameMenu extends GameMenu {

    public FFAGameMenu() {
        super("ffa", "FFA");
        addQueueItem("classic", "§bClassic");
        addQueueItem("op", "§bOP");
        addQueueItem("soup", "§bSoup");
        addQueueItem("uhc", "§bUHC");
        addItemToDisplay(getSlot(9, 1), new GameShopItem("ffa_shop"));
    }
}
