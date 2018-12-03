package me.rellynn.foxmc.treewars.shops.levels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.treewars.game.TWCurrency;

import java.util.List;

/**
 * Created by gwennaelguich on 01/06/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public class ShopLevel {
    private int cost;
    private TWCurrency currency;
    private String title;
    private List<String> lore;
}
