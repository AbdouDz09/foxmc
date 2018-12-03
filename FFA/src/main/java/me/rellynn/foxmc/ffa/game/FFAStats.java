package me.rellynn.foxmc.ffa.game;

import lombok.Getter;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
@Getter
public class FFAStats {
    private int killStreak;

    public void incrementKillStreak() {
        killStreak++;
    }

    public void resetKillStreak() {
        killStreak = 0;
    }
}
