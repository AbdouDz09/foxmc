package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class ParachuteGadget extends Gadget {
    private Map<UUID, Set<Entity>> entities = new HashMap<>();

    public ParachuteGadget() {
        super("parachute", 20, -1);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1400;
        String title = "§6Parachute";
        ItemStack icon = new ItemStack(Material.LEASH);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Launch you in the air and", "§7deploy your parachute.", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this gadget"), icon));
        return levels;
    }

    @EventHandler
    public void onEntityUnleash(EntityUnleashEvent evt) {
        if (entities.values().stream().anyMatch(entities -> entities.contains(evt.getEntity()))) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    evt.getEntity().getNearbyEntities(1.0D, 1.0D, 1.0D).stream().filter(entity -> entity instanceof Item && ((Item) entity).getItemStack().getType() == Material.LEASH).forEach(Entity::remove);
                }
            }.runTask(HubPlugin.get());
        }
    }

    @Override
    public void onStart(Player player) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        firework.setVelocity(new Vector(0.0D, 3.5D, 0.0D));
        firework.setPassenger(player);
        entities.put(player.getUniqueId(), new HashSet<Entity>() {{
            add(firework);
        }});
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        Set<Entity> entities = this.entities.get(player.getUniqueId());
        if (ticksLeft < -30) {
            if (player.isOnGround()) {
                HubAPI.get().getGadgetsManager().removeGadget(player);
            } else if (player.getVelocity().getY() < -0.3D) {
                player.setVelocity(new Vector(0.0D, player.getVelocity().getY() + 0.1D, 0.0D));
                entities.forEach(entity -> entity.setVelocity(entity.getVelocity().setX(0.0D).setZ(0.0D)));
            }
        } else if (ticksLeft == -30) {
            Location location = player.getLocation();
            entities.forEach(Entity::remove);
            player.setVelocity(new Vector());
            for (int i = 0; i < 10; i++) {
                Chicken chicken = location.getWorld().spawn(player.getLocation().add(Math.random() * 0.2D, 8.0D, Math.random() * 0.2D), Chicken.class);
                chicken.setVelocity(new Vector());
                chicken.setLeashHolder(player);
                entities.add(chicken);
            }
        }
    }

    @Override
    public void onEnd(Player player) {
        entities.remove(player.getUniqueId()).forEach(Entity::remove);
        player.setVelocity(new Vector(0.0D, 0.15D, 0.0D));
    }
}
