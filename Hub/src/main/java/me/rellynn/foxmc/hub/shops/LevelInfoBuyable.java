package me.rellynn.foxmc.hub.shops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public class LevelInfoBuyable {
    private Currency currency;
    private int price;
    private String title;
    private List<String> lore;
    private ItemStack icon;
}
