package me.rellynn.foxmc.hub.shops.items;

import lombok.Getter;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
@Getter
public abstract class ItemBuyable extends VirtualItem {
    private static final String PREFIX = "§8[§6Shop§8] §r";

    protected String shopId;
    protected String shopItemId;
    protected Map<Integer, LevelInfoBuyable> levels;

    public ItemBuyable(String shopId, String shopItemId) {
        super(shopId + "_" + shopItemId);
        this.shopId = shopId;
        this.shopItemId = shopItemId;
        this.levels = buildBuyableLevels();
    }

    public boolean canUse(Player player, int level) {
        return level >= levels.size();
    }

    protected abstract void onUse(Player player);

    protected abstract Map<Integer, LevelInfoBuyable> buildBuyableLevels();

    @Override
    public void onClick(Player player, PerformedAction action) {
        PlayerData data = FoxAPI.getPlayer(player);
        int nextShopLevel = data.getShopLevel(shopId, shopItemId) + 1;
        if (canUse(player, nextShopLevel)) {
            onUse(player);
            return;
        }
        if (nextShopLevel < levels.size()) {
            if (action != PerformedAction.INVENTORY_RIGHT) {
                player.sendMessage(PREFIX + "§cUse the §eright click §cto §aconfirm §cyou want to buy this item.");
                return;
            }
            LevelInfoBuyable levelInfo = levels.get(nextShopLevel);
            if (data.getAmount(levelInfo.getCurrency().getId()) < levelInfo.getPrice()) {
                player.sendMessage(PREFIX + "§cYou don't have enough " + levelInfo.getCurrency().getName() + "§c.");
                return;
            }
            data.decreaseAmount(levelInfo.getCurrency().getId(), levelInfo.getPrice(), (balance, diff, err) -> {
                if (err != null) {
                    player.sendMessage(PREFIX + "§cAn error occurred, please try again later!");
                    HubPlugin.get().getLogger().severe(player.getName() + " tried to buy " + shopItemId + " but an error occurred:");
                    err.printStackTrace();
                    return;
                }
                data.setShopLevel(shopId, shopItemId, nextShopLevel);
                player.sendMessage(PREFIX + "§aYou purchased: " + levelInfo.getTitle());
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                uglyUpdate(player);
            });
        }
    }
}
