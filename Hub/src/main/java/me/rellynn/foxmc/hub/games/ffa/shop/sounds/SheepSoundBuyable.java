package me.rellynn.foxmc.hub.games.ffa.shop.sounds;

import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableOnceEquippable;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.COINS;

/**
 * Created by gwennaelguich on 19/08/2017.
 * FoxMC Network.
 */
public class SheepSoundBuyable extends ItemBuyableOnceEquippable {

    SheepSoundBuyable() {
        super("ffa.sounds", Sound.SHEEP_IDLE.name());
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 500;
        ItemStack icon = new SpawnEgg(EntityType.SHEEP).toItemStack(1);
        levels.put(0, new LevelInfoBuyable(COINS, 0, "§c* §lNEW §c* §rSheep", Arrays.asList("§7Baaa", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, "§rSheep", Arrays.asList("§aYou own this sound"), icon));
        return levels;
    }
}
