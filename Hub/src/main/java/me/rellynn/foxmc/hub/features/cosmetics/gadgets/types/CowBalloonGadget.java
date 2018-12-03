package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class CowBalloonGadget extends Gadget {
    private Map<UUID, Entity> entities = new HashMap<>();

    public CowBalloonGadget() {
        super("cow_balloon", Rank.VIP_PLUS, 5, 30);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Cow Balloon", Arrays.asList("§7A fun cow balloon.", "§cRequires " + Rank.VIP_PLUS.getName()), new ItemStack(Material.LEATHER)));
        return levels;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        if (evt.getRightClicked() == entities.get(evt.getPlayer().getUniqueId())) {
            evt.setCancelled(true);
            ((LivingEntity) evt.getRightClicked()).setLeashHolder(evt.getPlayer());
        }
    }

    @Override
    public void onStart(Player player) {
        Location location = player.getLocation().add(0.0D, 2.7D, -1.5D);
        Cow cow = player.getWorld().spawn(location, MushroomCow.class);
        cow.setBaby();
        cow.setLeashHolder(player);
        entities.put(player.getUniqueId(), cow);
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        if (ticksLeft % 3 == 0) {
            Location location = player.getLocation().add(0.0D, 2.7D, -1.5D);
            Entity entity = entities.get(player.getUniqueId());
            entity.teleport(location);
            entity.setVelocity(player.getVelocity());
        }
    }

    @Override
    public void onEnd(Player player) {
        entities.remove(player.getUniqueId()).remove();
    }
}
