package me.rellynn.foxmc.hub.features.settings;

import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.Setting;
import me.rellynn.foxmc.api.settings.SettingValue;
import org.bukkit.DyeColor;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
public class SettingUtils {

    public static String getValue(Setting setting, PlayerData data) {
        SettingValue value = setting.get(data);
        switch (value) {
            case ENABLED:
                return "§aEnabled";
            case DISABLED:
                return "§cDisabled";
            case EVERYONE:
                return "§aEveryone";
            case FRIENDS:
                return "§6Friends";
            default:
                return "§eUnknown";
        }
    }

    public static DyeColor getColor(Setting setting, PlayerData data) {
        SettingValue value = setting.get(data);
        switch (value) {
            case ENABLED:
            case EVERYONE:
                return DyeColor.GREEN;
            case DISABLED:
                return DyeColor.RED;
            case FRIENDS:
                return DyeColor.ORANGE;
            default:
                return DyeColor.YELLOW;
        }
    }
}
