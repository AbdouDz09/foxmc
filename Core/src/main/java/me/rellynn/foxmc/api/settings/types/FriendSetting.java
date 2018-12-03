package me.rellynn.foxmc.api.settings.types;

import me.rellynn.foxmc.api.settings.Setting;

import java.util.Arrays;

import static me.rellynn.foxmc.api.settings.SettingValue.*;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
public class FriendSetting extends Setting {

    public FriendSetting(String name) {
        super(name, Arrays.asList(EVERYONE, FRIENDS, DISABLED));
    }
}
