package me.rellynn.foxmc.hub.main.game;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.matches.packets.JoinMatchRequestPacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class MatchDataItem extends VirtualItem {
    private MatchData data;
    private ItemStack item;

    MatchDataItem(MatchData data) {
        super(data.getName());
        this.data = data;
    }

    void refreshItem() {
        item = new ItemBuilder(Material.STAINED_CLAY, data.isJoinable() ? DyeColor.LIME.getWoolData() : DyeColor.RED.getWoolData())
                .setTitle(data.getName())
                .addLore("")
                .addLore("§7Game: §e" + data.getGame())
                .addLore("§7Map: §b" + data.getMap())
                .addLore("")
                .addLore("§7Players: §e" + data.getPlayers() + "§7/§b" + data.getMaxPlayers())
                .addLore("")
                .addLore(data.getState())
                .build();
    }

    @Override
    protected void onClick(Player player, PerformedAction action) {
        FProtocolManager.get().sendToServer(data.getServer().getName(), new JoinMatchRequestPacket(data, player.getUniqueId()));
    }

    @Override
    public ItemStack getItem(Player player) {
        return item;
    }
}
