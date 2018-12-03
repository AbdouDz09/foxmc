package me.rellynn.foxmc.bukkitapi.utils;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
@Getter
public class Hologram {
    private EntityArmorStand armorStand;
    private Packet spawnPacket;
    private Packet destroyPacket;

    public Hologram(String text, Location location) {
        this.armorStand = new EntityArmorStand(null);
        this.armorStand.setGravity(false);
        this.armorStand.setInvisible(true);
        this.armorStand.setSmall(true);
        this.armorStand.setCustomName(text);
        this.armorStand.setCustomNameVisible(true);
        this.destroyPacket = new PacketPlayOutEntityDestroy(armorStand.getId());
        move(location);
    }

    public void update(String text) {
        armorStand.setCustomName(text);
        Packet metaPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> PacketUtils.sendPacket(player, metaPacket));
    }

    public void move(Location location) {
        armorStand.spawnIn(((CraftWorld) location.getWorld()).getHandle());
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        spawnPacket = new PacketPlayOutSpawnEntityLiving(armorStand);
    }

    public void show(Player player) {
        PacketUtils.sendPacket(player, spawnPacket);
    }

    public void hide(Player player) {
        PacketUtils.sendPacket(player, destroyPacket);
    }

    public void spawn() {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::show);
    }

    public void destroy() {
        armorStand.die();
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> PacketUtils.sendPacket(player, destroyPacket));
    }
}
