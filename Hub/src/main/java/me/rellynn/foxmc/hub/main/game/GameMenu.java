package me.rellynn.foxmc.hub.main.game;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public abstract class GameMenu extends VirtualMenu {
    @Getter private String gameType;
    @Getter private String title;
    private Set<GameQueueItem> queues = new HashSet<>();

    public GameMenu(String gameType, String title) {
        super(gameType + "_menu", "Games > " + title, 20L);
        this.gameType = gameType;
        this.title = title;
    }

    protected void addQueueItem(String mode, String queueTitle) {
        queues.add(new GameQueueItem(this, mode, queueTitle));
    }

    @Override
    protected void onUpdate() {
        for (int slot = 9; slot < size * 9; slot++) {
            removeItem(slot);
        }
        int slot = 9 + (int) Math.floor((9 - queues.size()) / (double) 2);
        for (GameQueueItem item : queues) {
            item.refreshItem();
            addItemToDisplay(slot, item);
            slot++;
        }
        int line = (int) Math.ceil(slot / (double) 9) + 1;
        addItemToDisplay(getSlot(9, line), HubItems.backMenuItem);
    }
}
