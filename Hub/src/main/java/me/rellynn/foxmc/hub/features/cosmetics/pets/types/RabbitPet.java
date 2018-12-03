package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityRabbit;
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
public class RabbitPet extends Pet {

    public RabbitPet() {
        super("rabbit");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Rabbit";
        ItemStack icon = ItemBuilder.getPlayerSkull("cec242e667aee44492413ef461b810cac356b74d8718e5cec1f892a6b43e5e1");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§74 rabbit feet? Wow So Much", "§7Luck!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        return Arrays.asList(new EntityRabbit(((CraftWorld) player.getWorld()).getHandle()));
    }
}
