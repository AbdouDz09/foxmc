package me.rellynn.foxmc.hub.features.settings.items;

import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.Setting;
import me.rellynn.foxmc.api.settings.types.ToggleSetting;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.features.settings.SettingUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class SettingItem extends VirtualItem {
    private ItemStack icon;
    private Setting setting;
    private String title;
    private String[] description;

    public SettingItem(ItemStack icon, Setting setting, String title, String[] description) {
        super("settings_" + setting.getName(), ActionType.CUSTOM, 3);
        this.icon = icon;
        this.setting = setting;
        this.title = title;
        this.description = description;
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        PlayerData data = FoxAPI.getPlayer(player);
        if (setting instanceof ToggleSetting) {
            ((ToggleSetting) setting).toggle(data);
        } else {
            int index = setting.getValues().indexOf(setting.get(data)) + 1;
            if (index == setting.getValues().size()) {
                index = 0;
            }
            setting.set(data, setting.getValues().get(index));
        }
        getUglyMenu(player).updateOpened(player);
        player.playSound(player.getEyeLocation(), Sound.CLICK, 1.0F, 1.0F);
    }

    @Override
    public ItemStack getItem(Player player) {
        String value = SettingUtils.getValue(setting, FoxAPI.getPlayer(player));
        return new ItemBuilder(icon)
                .setTitle(value.substring(0, 2) + title + " - §l" + value.substring(2, value.length()))
                .addLore(description)
                .build();
    }
}
