package me.rellynn.foxmc.api.hubs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.api.servers.ServerData;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum HubLevel {
    LOW(25, 3),
    MEDIUM(40, 5),
    CHARGED(85, 4),
    FULL(100, 1),
    EMERGENCY(120, 0);

    public static HubLevel getLevel(ServerData serverData) {
        int percent = (int) Math.round((serverData.getPlayers() / (double) serverData.getMaxPlayers()) * 100);
        for (HubLevel level : values()) {
            if (percent < level.percent) {
                return level;
            }
        }
        return EMERGENCY;
    }

    private int percent;
    private int weight;
}
