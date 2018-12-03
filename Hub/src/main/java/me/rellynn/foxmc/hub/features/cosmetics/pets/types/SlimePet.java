package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntitySlime;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 22/05/2017.
 * FoxMC Network.
 */
public class SlimePet extends Pet {

    public SlimePet() {
        super("slime");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Slime";
        ItemStack icon = ItemBuilder.getPlayerSkull("16ad20fc2d579be250d3db659c832da2b478a73a698b7ea10d18c9162e4d9b5");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Be careful when you touch me!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntitySlime slime = new EntitySlime(((CraftWorld) player.getWorld()).getHandle());
        slime.setSize(1);
        return Arrays.asList(slime);
    }
}
