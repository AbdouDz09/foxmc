package me.rellynn.foxmc.api.players;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 20/04/2017.
 * FoxMC Network.
 */
@AllArgsConstructor
@Getter
public enum Rank {
    OWNER("owner", "§cOwner", "§c[OWN] ", 4, 1.0F, true),
    ADMIN("admin", "§cAdmin", "§c[ADM] ", 3, 1.0F, true),
    MODERATOR("mod", "§6Moderator", "§6[MOD] ", 2, 0.5F, true),
    HELPER("helper", "§9Helper", "§9[HELPER] ", 1, 0.5F, true),
    YOUTUBER("yt", "§fYou§cTuber", "§6[YT] §e", 5, 0.5F, false),
    FOX_PLUS("fox+", "§bFOX§5+", "§b[FOX§5+§b] ", 4, 0.5F, false),
    FOX("fox", "§bFOX", "§b[FOX] ", 3, 0.4f, false),
    VIP_PLUS("vip+", "§aVIP§e+", "§a[VIP§e+§a] ", 2, 0.25F, false),
    VIP("vip", "§aVIP", "§a[VIP] ", 1, 0.15F, false),
    DEFAULT("default", "§7Default", "§7", 0, 0.0F, false);

    public static Rank getById(String id) {
        return Arrays.stream(Rank.values()).filter(rank -> rank.getId().equalsIgnoreCase(id)).findFirst().orElse(Rank.DEFAULT);
    }

    private String id;
    private String name;
    private String prefix;
    private int level;
    private float coinsBooster;
    private boolean isMod;

    public boolean isHigherThan(Rank rank) {
        return isMod() && !rank.isMod() || getLevel() > rank.getLevel();
    }
}
