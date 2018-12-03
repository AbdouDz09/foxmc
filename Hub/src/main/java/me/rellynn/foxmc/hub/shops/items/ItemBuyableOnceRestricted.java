package me.rellynn.foxmc.hub.shops.items;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public abstract class ItemBuyableOnceRestricted extends ItemBuyableOnce {
    protected Rank rank;

    public ItemBuyableOnceRestricted(String shopId, String shopItemId, Rank rank) {
        super(shopId, shopItemId);
        this.rank = rank;
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        if (rank != Rank.DEFAULT && !FoxAPI.getPlayer(player).isAtLeast(rank)) {
            player.playSound(player.getEyeLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            player.sendMessage("§cYou must be " + rank.getName() + " §cor higher to use this!");
            player.sendMessage("§6Store: §e§ostore.foxmc.net");
            return;
        }
        super.onClick(player, action);
    }
}
