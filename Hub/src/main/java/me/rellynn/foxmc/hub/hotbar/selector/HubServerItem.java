package me.rellynn.foxmc.hub.hotbar.selector;

import me.rellynn.foxmc.api.hubs.HubLevel;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class HubServerItem extends VirtualItem {
    private ServerData data;
    private ItemStack item;

    HubServerItem(ServerData data) {
        super(data.getName(), ActionType.CUSTOM, "");
        this.data = data;
    }

    void refreshItem() {
        HubLevel level = HubLevel.getLevel(data);
        boolean isThisOne = data.equals(FoxAPI.get().getServerData());
        DyeColor color = isThisOne ? DyeColor.RED : (level == HubLevel.LOW || level == HubLevel.MEDIUM ? DyeColor.GREEN : (level == HubLevel.CHARGED ? DyeColor.ORANGE : DyeColor.RED));
        ItemBuilder builder = new ItemBuilder(Material.STAINED_CLAY, Math.max(1, Math.min(data.getPlayers(), 64)), color.getWoolData())
                .setTitle("§rHub " + data.getName().replace("hub", ""))
                .addLore("§7Players: " + (level == HubLevel.LOW ? "§aLow" : (level == HubLevel.MEDIUM ? "§aMedium" : (level == HubLevel.CHARGED ? "§6Charged" : "§cFull"))) + " §8(§r" + data.getPlayers() + "§8)");
        if (isThisOne) {
            builder.addLore("§cYou're here!");
        }
        item = builder.build();
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        if (data.equals(FoxAPI.get().getServerData())) {
            player.sendMessage("§cYou're already connected!");
            return;
        }
        HubLevel level = HubLevel.getLevel(data);
        if (level == HubLevel.EMERGENCY) {
            player.sendMessage("§cPlease choose another hub, this one is full!");
            return;
        } else if (level == HubLevel.FULL && !FoxAPI.getPlayer(player).isAtLeast(Rank.VIP)) {
            player.sendMessage("§cThis hub is full, buy " + Rank.VIP.getName() + " §cto join full servers!");
            return;
        }
        FoxAPI.get().getNetworkHandler().sendPlayer(player, data);
    }

    @Override
    public ItemStack getItem(Player player) {
        return item;
    }
}
