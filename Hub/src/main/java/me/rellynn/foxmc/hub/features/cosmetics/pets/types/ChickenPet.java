package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityChicken;
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
public class ChickenPet extends Pet {

    public ChickenPet() {
        super("chicken");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Chicken";
        ItemStack icon = ItemBuilder.getPlayerSkull("1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Cluck Cluck", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityChicken chicken = new EntityChicken(((CraftWorld) player.getWorld()).getHandle());
        chicken.setAge(-9999);
        return Arrays.asList(chicken);
    }
}
