package me.rellynn.foxmc.hub.npcentities;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.Hologram;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 26/05/2017.
 * FoxMC Network.
 */
@Getter
public class NPC {
    private int id;
    private EntityType type;
    private Location location;
    private String name;
    private ClickAction actionType = ClickAction.NOTHING;
    private String actionValue = "";

    private EntityLiving entity;
    private List<Packet> spawnPackets = new ArrayList<>();
    private List<Packet> destroyPackets = new ArrayList<>();

    NPC(int id, EntityType type, Location location, EntityLiving entity) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.entity = entity;
        spawnPackets.add(new PacketPlayOutSpawnEntityLiving(entity));
        destroyPackets.add(new PacketPlayOutEntityDestroy(entity.getId()));
    }

    public void spawn(Player player) {
        spawnPackets.forEach(packet -> PacketUtils.sendPacket(player, packet));
    }

    public void destroy() {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> destroyPackets.forEach(packet -> PacketUtils.sendPacket(player, packet)));
    }

    public void setName(String name) {
        this.name = name;
        // Create new packets
        String[] parts = name.split("\\n");
        ArrayUtils.reverse(parts);
        for (int i = 0; i < parts.length; i++) {
            Location where = location.clone().add(0.0D, (entity.getHeadHeight() - 1.7D) + (i * 0.3D), 0.0D);
            Hologram hologram = new Hologram(ChatColor.translateAlternateColorCodes('&', parts[i]), where);
            hologram.spawn();
            spawnPackets.add(hologram.getSpawnPacket());
            destroyPackets.add(hologram.getDestroyPacket());
        }
    }

    public boolean setEquipment(int slot, ItemStack item) {
        if (slot >= entity.getEquipment().length) {
            return false;
        }
        net.minecraft.server.v1_8_R3.ItemStack itemCopy = CraftItemStack.asNMSCopy(item);
        entity.setEquipment(slot, itemCopy);
        Packet packet = new PacketPlayOutEntityEquipment(entity.getId(), slot, itemCopy);
        PacketUtils.broadcastPacket(packet);
        spawnPackets.add(packet);
        return true;
    }

    public void setInteractAction(ClickAction action, String value) {
        this.actionType = action;
        this.actionValue = value;
    }

    public enum ClickAction {
        NOTHING,
        JOIN_SERVER,
        OPEN_MENU,
        MESSAGE
    }
}
