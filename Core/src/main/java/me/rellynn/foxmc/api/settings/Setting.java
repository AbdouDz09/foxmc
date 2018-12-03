package me.rellynn.foxmc.api.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;

import java.util.List;

import static me.rellynn.foxmc.api.settings.SettingValue.DISABLED;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public class Setting {
    protected String name;
    protected List<SettingValue> values;
    protected Rank rank;

    public Setting(String name, List<SettingValue> values) {
        this(name, values, Rank.DEFAULT);
    }

    public void set(PlayerData data, SettingValue value) {
        if (values.contains(value)) {
            data.setSetting(name, value.getValue());
        }
    }

    public SettingValue get(PlayerData data) {
        if (rank != Rank.DEFAULT && !data.isAtLeast(rank)) {
            return DISABLED;
        }
        String value = data.getSetting(name);
        return values.stream().filter(settingValue -> settingValue.getValue().equals(value)).findFirst().orElse(values.get(0));
    }

    public boolean is(PlayerData data, SettingValue value) {
        if (rank != Rank.DEFAULT && !data.isAtLeast(rank)) {
            return value == DISABLED;
        }
        return values.contains(value) && data.isSetting(name, value.getValue(), values.get(0).getValue());
    }
}
