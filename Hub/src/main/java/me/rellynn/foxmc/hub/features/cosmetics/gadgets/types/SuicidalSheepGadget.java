package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class SuicidalSheepGadget extends Gadget {
    private Map<UUID, Set<Entity>> entities = new HashMap<>();

    public SuicidalSheepGadget() {
        super("suicidal_sheep", 20, 10);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Suicidal Sheep";
        ItemStack icon = new ItemStack(Material.WOOL, 1, (short) DyeColor.RED.getWoolData());
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Launch an explosive sheep!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this gadget"), icon));
        return levels;
    }

    @Override
    public void onStart(Player player) {
        Location location = player.getLocation();
        Sheep sheep = location.getWorld().spawn(location, Sheep.class);
        sheep.setColor(DyeColor.RED);
        sheep.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
        location.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 1);
        entities.put(player.getUniqueId(), new HashSet<Entity>() {{
            add(sheep);
        }});
    }

    private Sheep getSheep(Player player) {
        return (Sheep) entities.get(player.getUniqueId()).stream().filter(entity -> entity instanceof Sheep).findFirst().get();
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        // Flicker
        if (ticksLeft > 90 && ticksLeft <= 150 && ticksLeft % 3 == 0) {
            Sheep sheep = getSheep(player);
            Location location = sheep.getLocation();
            sheep.setColor(sheep.getColor() == DyeColor.RED ? DyeColor.WHITE : DyeColor.RED);
            sheep.getWorld().playSound(location, Sound.NOTE_STICKS, 1.4F, 1.5F);
            sheep.getWorld().spigot().playEffect(location, Effect.TILE_BREAK, Material.WOOL.getId(), sheep.getColor().getWoolData(), 0, 0.3f, 0, 0, 5, 16);
        }
        // Blood animation
        else if (ticksLeft == 90) {
            Sheep sheep = getSheep(player);
            Location location = sheep.getLocation();
            sheep.remove();
            sheep.getWorld().createExplosion(location, 0);
            List<ItemStack> drops = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                drops.add(new ItemBuilder(Material.REDSTONE).setTitle("#" + i + "1").build());
                drops.add(new ItemBuilder(Material.PORK).setTitle("#" + i + "2").build());
            }
            drops.forEach(itemStack -> {
                Item item = sheep.getWorld().dropItem(location, itemStack);
                final float x = (float) -0.2 + (float) (Math.random() * 1.4);
                final float y = (float) -0.2 + (float) (Math.random() * 1.4);
                final float z = (float) -0.2 + (float) (Math.random() * 1.4);
                item.setVelocity(new Vector(x, y, z));
                item.setPickupDelay(Integer.MAX_VALUE);
                entities.get(player.getUniqueId()).add(item);
            });
        }
    }

    @Override
    public void onEnd(Player player) {
        entities.remove(player.getUniqueId()).forEach(Entity::remove);
    }
}
