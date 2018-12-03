package me.rellynn.foxmc.bungeeapi.utils;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class StringUtils {

    /**
     * Join all parts of a string array into one string.
     *
     * @param parts     The parts
     * @param separator The separator
     * @return The final string
     */
    public static String join(String[] parts, String separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : parts) {
            builder.append(s).append(separator);
        }
        int length = builder.length() - separator.length();
        return length <= 0 ? builder.toString() : builder.toString().substring(0, length);
    }
}
