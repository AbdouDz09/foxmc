package me.rellynn.foxmc.hub.shops.items;

import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public abstract class ItemBuyableOnceEquippable extends ItemBuyableOnce {

    public ItemBuyableOnceEquippable(String shopId, String shopItemId) {
        super(shopId, shopItemId);
    }

    @Override
    public void onUse(Player player) {
        PlayerData data = FoxAPI.getPlayer(player);
        data.setCurrentItem(shopId, !data.isCurrentItem(shopId, shopItemId) ? shopItemId : null);
        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        uglyUpdate(player);
    }

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder builder = new ItemBuilder(super.getItem(player));
        PlayerData data = FoxAPI.getPlayer(player);
        if (data.hasShopItem(shopId, shopItemId)) {
            builder.addLore("").addLore(data.isCurrentItem(shopId, shopItemId) ? "§b§oEquipped" : "§eClick to equip!");
        }
        return builder.build();
    }
}
