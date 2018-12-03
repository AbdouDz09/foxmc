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
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class CatSoundBuyable extends ItemBuyableOnceEquippable {

    CatSoundBuyable() {
        super("ffa.sounds", Sound.CAT_MEOW.name());
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 500;
        String title = "§rCat";
        ItemStack icon = new SpawnEgg(EntityType.OCELOT).toItemStack(1);
        levels.put(0, new LevelInfoBuyable(COINS, 0, title, Arrays.asList("§7Meow", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, title, Arrays.asList("§aYou own this sound"), icon));
        return levels;
    }
}
