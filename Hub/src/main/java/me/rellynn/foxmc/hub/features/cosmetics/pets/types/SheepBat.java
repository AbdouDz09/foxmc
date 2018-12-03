package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 23/05/2017.
 * FoxMC Network.
 */
public class SheepBat extends Pet {

    public SheepBat() {
        super("sheep");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1300;
        String title = "§6Sheep";
        ItemStack icon = ItemBuilder.getPlayerSkull("f31f9ccc6b3e32ecf13b8a11ac29cd33d18c95fc73db8a66c5d657ccb8be70");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Baaaaa", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntitySheep sheep = new EntitySheep(((CraftWorld) player.getWorld()).getHandle());
        sheep.setAge(-9999);
        return Arrays.asList(sheep);
    }
}
