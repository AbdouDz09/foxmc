package me.rellynn.foxmc.hub.main.game;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class MatchesMenu extends VirtualMenu {
    private GameMenu menu;
    private Map<MatchData, MatchDataItem> items = new HashMap<>();

    MatchesMenu(GameMenu menu, String mode, String queueTitle) {
        super(menu.getGameType() + "_" + mode + "_matches", menu.getTitle() + ": " + ChatColor.stripColor(queueTitle), 20L);
        this.menu = menu;
    }

    @Override
    protected void onUpdate() {
        for (int slot = 9; slot < size * 9; slot++) {
            removeItem(slot);
        }
        List<MatchData> matches = new ArrayList<>(FoxAPI.get().getMatchesHandler().filterMatches("game:" + menu.getGameType()));
        matches.sort(Comparator.comparingInt(MatchData::getPlayers).reversed());
        int slot = 9;
        for (MatchData match : matches) {
            MatchDataItem item = items.computeIfAbsent(match, MatchDataItem::new); // Take the existing item or create one
            item.refreshItem();
            addItemToDisplay(slot, item);
            slot++;
        }
        int line = (int) Math.ceil(slot / (double) 9) + 1;
        addItemToDisplay(getSlot(9, line), HubItems.backMenuItem);
        // Unregister old matches
        items.entrySet().removeIf(entry -> {
            if (!matches.contains(entry.getKey())) {
                entry.getValue().unregister();
                return true;
            }
            return false;
        });
    }
}
