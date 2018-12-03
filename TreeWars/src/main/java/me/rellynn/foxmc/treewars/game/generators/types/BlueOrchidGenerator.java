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
public class BlueOrchidGenerator extends Generator {

    public BlueOrchidGenerator(String id, Location location) {
        super(id, location, TWCurrency.BLUE_ORCHID, true);
    }

    @Override
    protected Map<Integer, GeneratorUpgrade> buildUpgrades() {
        Map<Integer, GeneratorUpgrade> upgrades = new HashMap<>();
        upgrades.put(0, new GeneratorUpgrade(TWCurrency.BLUE_ORCHID, 0, 500));
        return upgrades;
    }
}
