package me.rellynn.foxmc.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
public abstract class TimeUtils {
    private static final Pattern PATTERN = Pattern.compile(
            "(?:([0-9]+)*y[a-z]*)?" + // years
                    "(?:([0-9]+)*mo[a-z]*)?" + // month
                    "(?:([0-9]+)*w[a-z]*)?" + // weeks
                    "(?:([0-9]+)*d[a-z]*)?" + // days
                    "(?:([0-9]+)*h[a-z]*)?" + // hours
                    "(?:([0-9]+)*m[a-z]*)?" + // minutes
                    "(?:([0-9]+)*s[a-z]*)?", // seconds
            Pattern.CASE_INSENSITIVE);

    /**
     * Parse a duration into {@link Long}.
     *
     * @param str The string to parse
     * @return The duration in milliseconds
     */
    public static long getDuration(String str) {
        long time = 0;
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.find()) {
            if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                time += Long.parseLong(matcher.group(1)) * 31536000000L; // years
            }
            if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                time += Long.parseLong(matcher.group(2)) * 2592000000L; // months
            }
            if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                time += Long.parseLong(matcher.group(3)) * 604800000L; // weeks
            }
            if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                time += Long.parseLong(matcher.group(4)) * 86400000L; // days
            }
            if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                time += Long.parseLong(matcher.group(5)) * 3600000; // hours
            }
            if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                time += Long.parseLong(matcher.group(6)) * 60000; // minutes
            }
            if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {
                time += Long.parseLong(matcher.group(7)) * 1000; // seconds
            }
        }
        if (time == 0) {
            throw new NumberFormatException("Null duration!");
        }
        return time;
    }

    /**
     * Parse the duration into a readable string.
     *
     * @param duration The duration in milliseconds
     * @return The readable duration
     */
    public static String getDuration(long duration) {
        String str = "";
        int seconds = (int) (duration / 1000);
        int years = 0;
        while (seconds >= 31536000) {
            years++;
            seconds -= 31536000;
        }
        if (years > 0) {
            str += years + " years ";
        }
        int months = 0;
        while (seconds >= 2592000) {
            months++;
            seconds -= 2592000;
        }
        if (months > 0) {
            str += months + " months ";
        }
        int days = 0;
        while (seconds >= 86400) {
            days++;
            seconds -= 86400;
        }
        if (days > 0) {
            str += days + " days ";
        }
        int hours = 0;
        while (seconds >= 3600) {
            hours++;
            seconds -= 3600;
        }
        if (hours > 0) {
            str += hours + " hours ";
        }
        int minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }
        if (minutes > 0) {
            str += minutes + " minutes ";
        }
        if (seconds > 0) {
            str += seconds + " seconds ";
        }
        return str.length() == 0 ? str : str.substring(0, str.length() - 1);
    }
}
