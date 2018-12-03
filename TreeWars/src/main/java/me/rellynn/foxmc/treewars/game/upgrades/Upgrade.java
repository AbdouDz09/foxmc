package me.rellynn.foxmc.treewars.game.upgrades;

import lombok.Getter;
import me.rellynn.foxmc.treewars.game.upgrades.types.FastRespawnUpgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.MiningFatigueUpgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.MiningSpeedUpgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.TrapUpgrade;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 01/06/2017.
 * FoxMC Network.
 */
public abstract class Upgrade {
    public static final Upgrade MINING_SPEED = new MiningSpeedUpgrade();
    public static final Upgrade TRAP = new TrapUpgrade();
    public static final Upgrade MINING_FATIGUE = new MiningFatigueUpgrade();
    public static final Upgrade FAST_RESPAWN = new FastRespawnUpgrade();

    @Getter private String id;
    @Getter private ItemStack icon;
    private Map<Integer, ShopLevel> levels = new HashMap<>();

    public Upgrade(String id, ItemStack icon) {
        this.id = id;
        this.icon = icon;
        this.levels = buildLevels();
    }

    protected abstract Map<Integer, ShopLevel> buildLevels();

    public ShopLevel getLevel(int index) {
        return levels.get(Math.min(index, levels.size() - 1));
    }

    public int getLevels() {
        return levels.size();
    }
}
