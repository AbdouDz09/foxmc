package me.rellynn.foxmc.treewars.game.generators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.treewars.game.TWCurrency;

/**
 * Created by gwennaelguich on 27/06/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public class GeneratorUpgrade {
    private TWCurrency currency;
    private int cost;
    private int ticks;
}
