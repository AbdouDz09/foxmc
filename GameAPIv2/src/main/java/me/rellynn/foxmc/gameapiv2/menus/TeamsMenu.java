package me.rellynn.foxmc.gameapiv2.menus;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.menus.items.JoinTeamItem;

/**
 * Created by gwennaelguich on 06/06/2017.
 * FoxMC Network.
 */
public class TeamsMenu extends VirtualMenu {

    TeamsMenu(Game game) {
        super(game.getMatch().getName() + "_teams", "Teams");
        int slot = 0;
        for (Team team : game.getArena().getTeams()) {
            addItemToDisplay(slot, new JoinTeamItem(game, team));
            slot++;
        }
    }
}
