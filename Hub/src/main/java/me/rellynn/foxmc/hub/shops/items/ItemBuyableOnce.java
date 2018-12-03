package me.rellynn.foxmc.hub.shops.items;

import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public abstract class ItemBuyableOnce extends ItemBuyable {

    public ItemBuyableOnce(String shopId, String shopItemId) {
        super(shopId, shopItemId);
    }

    @Override
    public void onUse(Player player) {
        player.sendMessage("§cYou already own this item!");
    }

    @Override
    public ItemStack getItem(Player player) {
        LevelInfoBuyable levelInfo = levels.get(Math.min(1, FoxAPI.getPlayer(player).getShopLevel(shopId, shopItemId)));
        return new ItemBuilder(levelInfo.getIcon())
                .setTitle(levelInfo.getTitle())
                .addLore(levelInfo.getLore())
                .build();
    }
}
