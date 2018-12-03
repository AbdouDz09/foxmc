package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class CloudGadget extends Gadget {
    private static final int PARTICLES = 15;
    private static final double INCREMENT = (2 * Math.PI) / PARTICLES;

    public CloudGadget() {
        super("cloud", 20, 10);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1100;
        String title = "§6Cloud";
        ItemStack icon = new ItemStack(Material.FEATHER);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Have you ever dreamed", "§7to walk on a cloud?", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this gadget"), icon));
        return levels;
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        if (ticksLeft % 10 == 0) {
            Location location = player.getLocation();
            for (double radius = 0; radius <= 1.0D; radius += 0.25D) {
                for (int i = 0; i < PARTICLES; i++) {
                    double angle = i * INCREMENT;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    player.getWorld().spigot().playEffect(new Location(player.getWorld(), location.getX() + x, location.getY(), location.getZ() + z), Effect.CLOUD, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
                }
            }
            player.getWorld().getNearbyEntities(player.getLocation(), 1.0D, 1.0D, 1.0D).forEach(entity -> {
                if (entity instanceof LivingEntity
                        && !(entity instanceof ArmorStand)
                        && (!(entity instanceof Player) || Settings.hubVisibility.is(FoxAPI.getPlayer((Player) entity), SettingValue.ENABLED))) {
                    entity.setVelocity(entity.getVelocity().add(new Vector(0, 0.5, 0)));
                }
            });
        }
    }
}
