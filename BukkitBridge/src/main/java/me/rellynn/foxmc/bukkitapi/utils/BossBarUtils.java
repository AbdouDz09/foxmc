package me.rellynn.foxmc.bukkitapi.utils;

import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public abstract class BossBarUtils {
    private static final Map<Player, EntityWither> players = new WeakHashMap<>();

    static {
        new BukkitRunnable() {

            @Override
            public void run() {
                players.forEach((player, wither) -> {
                    setWitherLocation(wither, player.getLocation());
                    PacketUtils.sendPacket(player, new PacketPlayOutEntityTeleport(wither));
                });
            }
        }.runTaskTimer(BridgePlugin.get(), 10, 10);
    }

    private static void setWitherLocation(EntityWither wither, Location location) {
        location.add(location.getDirection().multiply(60));
        wither.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
    }

    /**
     * Set a boss bar to a player.
     * It can be used to show various informations.
     *
     * @param player The player
     * @param text   The text
     */
    public static void setBar(Player player, String text) {
        EntityWither wither = players.get(player);
        boolean create = wither == null;
        if (create) {
            Location location = player.getLocation();
            wither = new EntityWither(((CraftWorld) location.getWorld()).getHandle());
            wither.setInvisible(true);
            setWitherLocation(wither, location);
            players.put(player, wither);
        }
        wither.setCustomName(text);
        Packet packet;
        if (create) {
            packet = new PacketPlayOutSpawnEntityLiving(wither);
        } else {
            packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
        }
        PacketUtils.sendPacket(player, packet);
    }

    /**
     * Remove a player boss bar.
     *
     * @param player The player
     */
    public static void removeBar(Player player) {
        EntityWither wither = players.remove(player);
        if (wither != null) {
            PacketUtils.sendPacket(player, new PacketPlayOutEntityDestroy(wither.getId()));
        }
    }
}
