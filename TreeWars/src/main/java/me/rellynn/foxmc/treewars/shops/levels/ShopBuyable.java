package me.rellynn.foxmc.treewars.shops.levels;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public abstract class ShopBuyable extends VirtualItem {

    public ShopBuyable(String id) {
        super("shop_" + id, VirtualItem.ActionType.CUSTOM, "");
    }

    public abstract ItemStack getShopItem(Player player);

    public abstract ShopLevel getLevel(Player player);

    public abstract void onPurchase(Player player, ShopLevel level);

    public boolean canPurchase(Player player, ShopLevel level) {
        return true;
    }

    @Override
    protected void onClick(Player player, VirtualItem.PerformedAction action) {
        ShopLevel level = getLevel(player);
        if (canPurchase(player, level)) {
            if (!level.getCurrency().takeItems(player, level.getCost())) {
                player.sendMessage("§cYou need " + level.getCurrency().format(level.getCost()) + " §cto purchase " + level.getTitle());
                return;
            }
            onPurchase(player, level);
        }
    }

    @Override
    public ItemStack getItem(Player player) {
        ShopLevel level = getLevel(player);
        List<String> lore = new ArrayList<>();
        if (level.getLore() != null && !level.getLore().isEmpty()) {
            lore.addAll(level.getLore());
            lore.add("");
        }
        lore.add("§7Cost: " + level.getCurrency().format(level.getCost()));
        return new ItemBuilder(getShopItem(player))
                .setTitle(level.getTitle())
                .addLore(lore)
                .build();
    }
}
