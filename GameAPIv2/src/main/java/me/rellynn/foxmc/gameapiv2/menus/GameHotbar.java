package me.rellynn.foxmc.gameapiv2.menus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.menus.items.HubItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class GameHotbar extends VirtualMenu {
    private TeamsMenu teamsMenu;

    public GameHotbar(Game game) {
        super(game.getMatch().getName() + "_hotbar", 9, "Hotbar");
        // Teams
        if (game.isUsingTeams()) {
            teamsMenu = new TeamsMenu(game);
            addItemToDisplay(0, new VirtualItem(game.getMatch().getName() + "_teams", VirtualItem.ActionType.OPEN_MENU, teamsMenu.getId()) {
                private final ItemStack ITEM = new ItemBuilder(Material.PAPER)
                        .setTitle("§aTeams")
                        .build();

                @Override
                public ItemStack getItem(Player player) {
                    return game.canChooseTeam(player) ? ITEM : null;
                }
            });
        }
        // Return to hub item
        addItemToDisplay(8, new HubItem(game));
    }

    public void unregister() {
        super.unregister();
        if (teamsMenu != null) {
            teamsMenu.unregister();
        }
    }
}
