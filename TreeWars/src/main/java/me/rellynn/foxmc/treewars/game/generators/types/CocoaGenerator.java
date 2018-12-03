package me.rellynn.foxmc.treewars.game.generators.types;

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
public class CocoaGenerator extends Generator {

    public CocoaGenerator(String id, Location location) {
        super(id, location, TWCurrency.COCOA, false);
    }

    @Override
    protected Map<Integer, GeneratorUpgrade> buildUpgrades() {
        Map<Integer, GeneratorUpgrade> upgrades = new HashMap<>();
        upgrades.put(0, new GeneratorUpgrade(TWCurrency.COCOA, 0, 70));
        upgrades.put(1, new GeneratorUpgrade(TWCurrency.COCOA, 15, 50));
        upgrades.put(2, new GeneratorUpgrade(TWCurrency.BLUE_ORCHID, 4, 40));
        upgrades.put(3, new GeneratorUpgrade(TWCurrency.BLUE_ORCHID, 10, 30));
        return upgrades;
    }
}
