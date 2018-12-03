package me.rellynn.foxmc.hub.hotbar.selector;

import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class HubSelector extends VirtualMenu {
    private Map<ServerData, HubServerItem> items = new HashMap<>();

    public HubSelector() {
        super("hub_selector", "Hub Selector", 20);
    }

    @Override
    protected void onUpdate() {
        clearDisplay();
        int slot = 0;
        List<ServerData> hubs = FoxAPI.get().getHubsManager().getHubs();
        for (ServerData data : hubs) {
            HubServerItem item = items.computeIfAbsent(data, HubServerItem::new);
            item.refreshItem();
            addItemToDisplay(slot, item);
            slot++;
        }
        int line = (int) Math.ceil(slot / (double) 9) + 1;
        addItemToDisplay(getSlot(9, line), HubItems.backMenuItem);
        // Remove old entries
        items.entrySet().removeIf(entry -> {
            if (!hubs.contains(entry.getKey())) {
                entry.getValue().unregister();
                return true;
            }
            return false;
        });
    }
}
