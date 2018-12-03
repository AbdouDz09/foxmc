package me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.effects.types.AroundHeadEffect;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class AngryEffect extends AroundHeadEffect {

    public AngryEffect() {
        super("angry", 2L, Rank.VIP);
        radius = 0.15D;
        angle = 90.0F;
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 700;
        String title = "§6Angry";
        ItemStack icon = new ItemStack(Material.SULPHUR);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Try to think positive.", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this effect"), icon));
        return levels;
    }

    @Override
    protected void onTick(Player player) {
        player.getWorld().spigot().playEffect(getLocation(player).subtract(0.0D, 0.35D, 0.0D), Effect.VILLAGER_THUNDERCLOUD, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
    }
}

