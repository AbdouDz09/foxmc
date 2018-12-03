package me.rellynn.foxmc.treewars.game.generators.types;

import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.game.generators.Generator;
import me.rellynn.foxmc.treewars.game.generators.GeneratorUpgrade;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 27/06/2017.
 * FoxMC Network.
 */
public class DeadBushGenerator extends Generator {

    public DeadBushGenerator(String id, Location location) {
        super(id, location, TWCurrency.DEAD_BUSH, true);
    }

    @Override
    protected Map<Integer, GeneratorUpgrade> buildUpgrades() {
        Map<Integer, GeneratorUpgrade> upgrades = new HashMap<>();
        upgrades.put(0, new GeneratorUpgrade(TWCurrency.DEAD_BUSH, 0, TWPlugin.getGame().isSoloMode() ? 700 : 900));
        return upgrades;
    }
}
