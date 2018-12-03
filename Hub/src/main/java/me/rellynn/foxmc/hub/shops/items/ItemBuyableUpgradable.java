package me.rellynn.foxmc.hub.shops.items;

import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.menus.UpgradeMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public abstract class ItemBuyableUpgradable extends ItemBuyable {
    private VirtualMenu levelsMenu;

    public ItemBuyableUpgradable(String shopId, String shopItemId, LevelInfoBuyable levelInfo) {
        super(shopId, shopItemId);
        this.levelsMenu = new UpgradeMenu(this, levelInfo);
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        if (action == PerformedAction.INVENTORY_LEFT) {
            levelsMenu.open(player);
            return;
        }
        super.onClick(player, action);
    }

    @Override
    protected void onUse(Player player) {
        player.sendMessage("§cYou purchased all upgrades!");
    }

    @Override
    public ItemStack getItem(Player player) {
        LevelInfoBuyable levelInfo = levels.get(FoxAPI.getPlayer(player).getShopLevel(shopId, shopItemId));
        return new ItemBuilder(levelInfo.getIcon())
                .setTitle(levelInfo.getTitle())
                .addLore(levelInfo.getLore())
                .addLore("§bLeft click: show upgrades")
                .build();
    }
}
