package me.rellynn.foxmc.ffa.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by gwennaelguich on 08/06/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum FFAMode {
    UHC("uhc", "§eUHC"),
    SOUP("soup", "§6Soup"),
    OP("op", "§cOP"),
    CLASSIC("classic", "§7Classic");

    public static FFAMode getById(String id) {
        for (FFAMode mode : values()) {
            if (mode.getId().equals(id)) {
                return mode;
            }
        }
        return FFAMode.CLASSIC;
    }

    private String id;
    private String name;
}
