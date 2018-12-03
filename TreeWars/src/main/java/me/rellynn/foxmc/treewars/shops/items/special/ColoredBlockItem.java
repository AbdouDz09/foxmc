package me.rellynn.foxmc.treewars.shops.items.special;

import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 27/06/2017.
 * FoxMC Network.
 */
public class ColoredBlockItem extends ShopItem {

    public ColoredBlockItem(String id, TWCurrency currency, int cost, ItemStack item, String title, String... lore) {
        super(id, currency, cost, item, title, lore);
    }

    /*
    Custom block color
     */
    @Override
    public ItemStack getShopItem(Player player) {
        ItemStack item = super.getShopItem(player).clone();
        Team team = TWPlugin.getGame().getArena().getPlayerTeam(player);
        if (team != null) {
            item.setDurability(DyeColor.valueOf(team.getId().toUpperCase()).getWoolData());
        }
        return item;
    }
}
