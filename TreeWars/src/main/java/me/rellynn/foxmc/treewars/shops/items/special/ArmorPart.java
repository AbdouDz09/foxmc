package me.rellynn.foxmc.treewars.shops.items.special;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.shops.items.ShopItem;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by gwennaelguich on 27/06/2017.
 * FoxMC Network.
 */
public class ArmorPart extends ShopItem {

    public ArmorPart(String id, TWCurrency currency, int cost, Material material, String title, String... lore) {
        super(id, currency, cost, new ItemBuilder(material).setUnbreakable(true).build(), title, lore);
    }

    @Override
    public void onPurchase(Player player, ShopLevel level) {
        player.sendMessage("§7You purchased " + level.getTitle() + " §8(Permanent)");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.6F, 1.2F);
        player.getInventory().setChestplate(getShopItem(player).clone());
        player.setMetadata("PERMANENT_ARMOR", new FixedMetadataValue(TWPlugin.get(), player.getInventory().getArmorContents()));
        player.updateInventory();
    }

    /*
    Custom leather color
     */
    @Override
    public ItemStack getShopItem(Player player) {
        ItemStack item = super.getShopItem(player);
        if (item.getItemMeta() instanceof LeatherArmorMeta) {
            item = item.clone();
            Team team = TWPlugin.getGame().getArena().getPlayerTeam(player);
            if (team != null) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                meta.setColor(DyeColor.valueOf(team.getId().toUpperCase()).getColor());
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}
