package me.rellynn.foxmc.hub.games.twars;

import me.rellynn.foxmc.hub.main.game.GameMenu;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
public class TWarsGameMenu extends GameMenu {

    public TWarsGameMenu() {
        super("twars", "TreeWars");
        addQueueItem("teams_cave", "§bTeams §e§7(Cave)");
        addQueueItem("teams_rainbow", "§bTeams §e§7(Rainbow)");
        addQueueItem("teams_end", "§bTeams §e§7(End)");
        addQueueItem("teams_ice", "§bTeams §e§7(Ice)");
        addQueueItem("solo_witch", "§bSolo §e§7(Witch)");
        addQueueItem("solo_desert", "§bSolo §e§7(Desert)");
    }
}
