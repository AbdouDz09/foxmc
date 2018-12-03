package me.rellynn.foxmc.bukkitapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Locale;

/**
 * Created by gwennaelguich on 17/04/2017.
 * FoxMC Network.
 */
public class LocationUtils {

    public static String toString(Location location) {
        return String.format(Locale.ENGLISH, "%s_%.1f_%.1f_%.1f_%d_%d", location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), (int) location.getYaw(), (int) location.getPitch());
    }

    public static Location toLocation(String s) {
        String[] splitted = s.split("_");
        return new Location(Bukkit.getWorld(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

    public static boolean hasMoved(Location from, Location to) {
        return from.getWorld() != to.getWorld() || from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}
