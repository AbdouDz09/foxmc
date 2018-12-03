package me.rellynn.foxmc.hub.shops;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum Currency {
    COINS("coins", "§6FoxCoins"),
    TAILS("tails", "§aTails");

    private String id;
    private String name;
}
