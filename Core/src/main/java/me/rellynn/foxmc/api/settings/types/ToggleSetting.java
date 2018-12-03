package me.rellynn.foxmc.api.settings.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.Setting;
import me.rellynn.foxmc.api.settings.SettingValue;

import java.util.Arrays;

import static me.rellynn.foxmc.api.settings.SettingValue.DISABLED;
import static me.rellynn.foxmc.api.settings.SettingValue.ENABLED;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
public class ToggleSetting extends Setting {

    public ToggleSetting(String name, Rank rank) {
        super(name, Arrays.asList(ENABLED, DISABLED), rank);
    }

    public ToggleSetting(String name) {
        this(name, Rank.DEFAULT);
    }

    public boolean toggle(PlayerData data) {
        SettingValue newValue = !is(data, ENABLED) ? ENABLED : DISABLED;
        set(data, newValue);
        return newValue == ENABLED;
    }
}
