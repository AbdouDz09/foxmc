package me.rellynn.foxmc.hub.features.settings.items;

import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.Setting;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.features.settings.SettingUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class SettingStateItem extends VirtualItem {
    private Setting setting;

    public SettingStateItem(Setting setting) {
        super("setting_" + setting.getName() + "_state", ActionType.NOTHING);
        this.setting = setting;
    }

    @Override
    public ItemStack getItem(Player player) {
        PlayerData data = FoxAPI.getPlayer(player);
        return new ItemBuilder(Material.STAINED_GLASS_PANE, SettingUtils.getColor(setting, data).getWoolData())
                .setTitle(SettingUtils.getValue(setting, data))
                .build();
    }
}
