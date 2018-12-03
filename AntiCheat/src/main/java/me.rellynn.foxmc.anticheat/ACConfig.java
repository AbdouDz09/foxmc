package me.rellynn.foxmc.anticheat;

/**
 * Created by gwennaelguich on 16/07/2017.
 * FoxMC Network.
 */
public class ACConfig {
    // General
    public static final boolean DEBUG_MODE = false;
    public static final long NOTIFY_ANTISPAM = 25000L;
    // Ping
    public static final int HIGH_PING = 225;
    public static final int HIGHEST_PING = 600;
    public static final int KEEP_LAST_PINGS = 5;
    public static final long PING_REFRESH = 5000L;
    // Checks
    public static final float BASE_JUMP_HEIGHT = 1.25F;
    public static final int MAX_MOVE_PACKETS = 22;
}
