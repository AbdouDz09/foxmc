package me.rellynn.foxmc.hub.main.game;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 20/08/2017.
 * FoxMC Network.
 */
public class GameShopItem extends VirtualItem {
    private static final ItemStack ITEM = new ItemBuilder(Material.EMERALD)
            .setTitle("§6Shop")
            .build();

    public GameShopItem(String shopId) {
        super(shopId + "_item", ActionType.OPEN_MENU, shopId);
    }

    @Override
    public ItemStack getItem(Player player) {
        return ITEM;
    }
}
