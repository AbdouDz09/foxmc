package me.rellynn.foxmc.api.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum SettingValue {
    ENABLED("enabled"),
    DISABLED("disabled"),
    EVERYONE("everyone"),
    FRIENDS("friends");

    private String value;
}
