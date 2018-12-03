package me.rellynn.foxmc.treewars.shops.items;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.levels.ShopBuyable;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class ShopItem extends ShopBuyable {
    private ItemStack item;
    private ShopLevel level;

    public ShopItem(String id, TWCurrency currency, int cost, ItemStack item, String title, String... lore) {
        super(id);
        this.item = item;
        this.level = new ShopLevel(cost, currency, title, Arrays.asList(lore));
    }

    @Override
    public ItemStack getShopItem(Player player) {
        return item;
    }

    @Override
    public ShopLevel getLevel(Player player) {
        return level;
    }

    @Override
    public void onPurchase(Player player, ShopLevel level) {
        player.sendMessage("§7You purchased " + level.getTitle());
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.6F, 1.2F);
        player.getInventory().addItem(getShopItem(player).clone());
        player.updateInventory();
    }
}
