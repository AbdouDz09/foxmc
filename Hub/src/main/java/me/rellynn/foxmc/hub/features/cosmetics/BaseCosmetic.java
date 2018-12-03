package me.rellynn.foxmc.hub.features.cosmetics;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableOnceRestricted;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 24/06/2017.
 * FoxMC Network.
 */
public abstract class BaseCosmetic extends ItemBuyableOnceRestricted {

    public BaseCosmetic(String shopId, String shopItemId, Rank rank) {
        super(shopId, shopItemId, rank);
    }

    @Override
    public boolean canUse(Player player, int level) {
        boolean result = super.canUse(player, level) || FoxAPI.getPlayer(player).getModLevel() > 0;
        if (result && Settings.hubVisibility.is(FoxAPI.getPlayer(player), SettingValue.DISABLED)) {
            player.sendMessage("§cEnable §aPlayer Visibility §cto use cosmetics!");
            return false;
        }
        return result;
    }
}
