package me.rellynn.foxmc.hub.features.cosmetics.pets;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.hub.HubPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
public class PetsManager extends BukkitRunnable implements Listener {
    private Map<String, Pet> pets = new HashMap<>();
    private Map<Player, Pet> playerPets = new ConcurrentHashMap<>();

    public PetsManager() {
        runTaskTimerAsynchronously(HubPlugin.get(), 0, 2);
        Bukkit.getPluginManager().registerEvents(this, HubPlugin.get());
        // Restore pets (reload case)
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> showPet(player));
            }
        }.runTask(HubPlugin.get());
    }

    void registerPet(Pet pet) {
        pets.put(pet.getShopItemId(), pet);
    }

    public boolean removePet(Player player) {
        Pet pet = playerPets.remove(player);
        if (pet != null) {
            pet.destroyEntities(player).forEach(entity -> ((WorldServer) entity.world).tracker.untrackEntity(entity));
            return true;
        }
        return false;
    }

    public void removePets() {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::removePet);
    }

    private void moveEntities(Player player, List<Entity> entities) {
        Location location = player.getLocation();
        double angle = Math.toRadians(location.getYaw() - 145);
        double x = 0.85D * Math.cos(angle);
        double z = 0.85D * Math.sin(angle);
        entities.forEach(entity -> {
            entity.setPosition(entity.lastX + location.getX() + x, entity.lastY + location.getY() + 1.5D, entity.lastZ + location.getZ() + z);
            entity.pitch = location.getPitch();
            entity.yaw = location.getYaw();
            entity.f(entity.yaw);
            entity.g(entity.yaw);
        });
    }

    public void showPet(Player player) {
        JsonElement petName = FoxAPI.getPlayer(player).getCustomData("hubs.pet");
        if (petName != null && pets.containsKey(petName.getAsString())) {
            Pet pet = pets.get(petName.getAsString());
            List<Entity> entities = pet.createPet(player);
            moveEntities(player, entities);
            EntityTracker tracker = ((CraftWorld) player.getWorld()).getHandle().tracker;
            entities.forEach(entity -> {
                entity.attachedToPlayer = true;
                tracker.track(entity);
            });
            playerPets.put(player, pet);
        }
    }

    public void setPet(Player player, Pet pet) {
        removePet(player);
        FoxAPI.getPlayer(player).setCustomData("hubs.pet", pet == null ? null : new JsonPrimitive(pet.getShopItemId()));
        showPet(player);
    }

    @Override
    public void run() {
        playerPets.forEach((player, pet) -> moveEntities(player, pet.getEntities(player)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        removePet(evt.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        if (Settings.hubVisibility.is(FoxAPI.getPlayer(player), SettingValue.ENABLED)) {
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            playerPets.forEach((target, pet) -> pet.getEntities(target).forEach(entity -> {
                EntityTracker tracker = ((WorldServer) entity.world).tracker;
                EntityTrackerEntry entry = tracker.trackedEntities.get(entity.getId());
                if (entry != null) {
                    entry.updatePlayer(nmsPlayer);
                }
            }));
            showPet(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent evt) {
        Player player = evt.getPlayer();
        Pet pet = playerPets.get(player);
        if (pet != null) {
            pet.getEntities(player).forEach(entity -> {
                EntityTracker tracker = ((WorldServer) entity.world).tracker;
                if (evt.isSneaking()) {
                    tracker.untrackEntity(entity);
                } else if (!tracker.trackedEntities.b(entity.getId())) {
                    tracker.track(entity);
                }
            });
        }
    }
}
