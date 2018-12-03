package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.entities.InstantFireworksEntity;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 24/06/2017.
 * FoxMC Network.
 */
public class JetpackGadget extends Gadget {
    private JetpackItem jetpackItem = new JetpackItem();

    public JetpackGadget() {
        super("jetpack", 25, 15);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent evt) {
        if (evt.getReason().contains("Fly") && HubAPI.get().getGadgetsManager().getGadget(evt.getPlayer()) == this) {
            evt.setCancelled(true);
        }
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1500;
        String title = "§6Jetpack";
        ItemStack icon = new ItemStack(Material.IRON_CHESTPLATE);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Propel you in the air!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this gadget"), icon));
        return levels;
    }

    @Override
    public void onStart(Player player) {
        jetpackItem.setItem(player, player.getInventory(), 38);
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        Location location = player.getLocation();
        (player.isInsideVehicle() ? player.getVehicle() : player).setVelocity(location.getDirection().normalize().multiply(0.35D));
        if (ticksLeft % 3 == 0)
            player.getWorld().spigot().playEffect(location, Effect.LARGE_SMOKE, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
    }

    @Override
    public void onEnd(Player player) {
        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
        InstantFireworksEntity fireworksEntity = new InstantFireworksEntity(nmsWorld);
        Firework firework = (Firework) fireworksEntity.getBukkitEntity();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.ORANGE).withFade(Color.YELLOW).build());
        firework.setFireworkMeta(meta);
        Location location = player.getLocation();
        fireworksEntity.setPosition(location.getX(), location.getY(), location.getZ());
        nmsWorld.addEntity(fireworksEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        player.getInventory().setChestplate(null);
        player.sendMessage("§eYour jetpack is out of fuel!");
    }

    private class JetpackItem extends VirtualItem {
        private final ItemStack ITEM = new ItemBuilder(Material.IRON_CHESTPLATE)
                .setTitle("§bJetpack")
                .build();

        JetpackItem() {
            super("gadgets.jetpack_item", ActionType.CUSTOM, "");
        }

        @Override
        protected void onClick(Player player, PerformedAction action) {
            HubAPI.get().getGadgetsManager().removeGadget(player);
        }

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    }
}

