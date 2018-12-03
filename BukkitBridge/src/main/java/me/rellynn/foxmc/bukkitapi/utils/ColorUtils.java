package me.rellynn.foxmc.bukkitapi.utils;

import org.bukkit.Color;


/**
 * Created by gwennaelguich on 18/04/2017.
 * FoxMC Network.
 */
public abstract class ColorUtils {
    private static final Color[] COLORS = {
            Color.AQUA, Color.BLUE,
            Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY,
            Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER,
            Color.WHITE, Color.TEAL, Color.YELLOW
    };

    public static Color getRandom() {
        return COLORS[(int) (Math.random() * COLORS.length)];
    }
}
