package me.rellynn.foxmc.hub.features.cosmetics.mounts;

import io.netty.channel.Channel;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.TinyProtocol;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.entities.*;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class MountsManager extends TinyProtocol {
    private static final Reflection.FieldAccessor<Boolean> UNMOUNT_FIELD = Reflection.getField(PacketPlayInSteerVehicle.class, "d", boolean.class);

    public static void registerEntities() {
        registerEntity("Spider", 52, EntitySpiderMount.class);
        registerEntity("Slime", 55, EntitySlimeMount.class);
        registerEntity("CaveSpider", 59, EntityCaveSpiderMount.class);
        registerEntity("LavaSlime", 62, EntitySnakeMount.class);
        registerEntity("Guardian", 68, EntityGuardianMount.class);
        registerEntity("Pig", 90, EntityPigMount.class);
        registerEntity("Sheep", 91, EntitySheepMount.class);
        registerEntity("Cow", 92, EntityCowMount.class);
        registerEntity("Chicken", 93, EntityChickenMount.class);
        registerEntity("Squid", 94, EntitySquidMount.class);
        registerEntity("Wolf", 95, EntityWolfMount.class);
        registerEntity("Ozelot", 98, EntityOcelotMount.class);
        registerEntity("EntityHorse", 100, EntityHorseMount.class);
        registerEntity("Rabbit", 101, EntityRabbitMount.class);
    }

    private static void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass) {
        try {
            List<Map<?, ?>> dataMaps = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }
            if (dataMaps.get(2).containsKey(id)) {
                dataMaps.get(0).remove(name);
                dataMaps.get(2).remove(id);
            }
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Spawn slime
     */
    public static void spawnSlimeMount(Player player, int size) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntitySlimeMount entity = new EntitySlimeMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setSize(size);
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn cow
     */
    public static void spawnCowMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityCowMount entity = new EntityCowMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn horse
     */
    public static void spawnHorseMount(Player player, Horse.Variant variant, Horse.Color color, Horse.Style style) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityHorseMount entity = new EntityHorseMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setTame(true);
        entity.setType(variant.ordinal());
        entity.setVariant(color.ordinal() & 255 | style.ordinal() << 8);
        entity.setOwnerUUID(player.getUniqueId().toString());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn pig
     */
    public static void spawnPigMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityPigMount entity = new EntityPigMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setSaddle(true);
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn sheep
     */
    public static void spawnSheepMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntitySheepMount entity = new EntitySheepMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setCustomName("jeb_");
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn ocelot
     */
    public static void spawnOcelotMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityOcelotMount entity = new EntityOcelotMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn wolf
     */
    public static void spawnWolfMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityWolfMount entity = new EntityWolfMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn spider
     */
    public static void spawnSpiderMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntitySpiderMount entity = new EntitySpiderMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn cave spider
     */
    public static void spawnCaveSpiderMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityCaveSpiderMount entity = new EntityCaveSpiderMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn rabbit
     */
    public static void spawnRabbitMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityRabbitMount entity = new EntityRabbitMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn squid
     */
    public static void spawnSquidMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntitySquidMount entity = new EntitySquidMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn chicken
     */
    public static void spawnChickenMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityChickenMount entity = new EntityChickenMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn guardian
     */
    public static void spawnGuardianMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntityGuardianMount entity = new EntityGuardianMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    /*
    Spawn snake
     */
    public static void spawnSnakeMount(Player player) {
        World world = ((CraftWorld) player.getWorld()).getHandle();
        EntitySnakeMount entity = new EntitySnakeMount(world);
        Location location = player.getLocation();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.summonTail(20);
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().setPassenger(player);
    }

    public MountsManager() {
        super(HubPlugin.get());
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if (packet instanceof PacketPlayInSteerVehicle && sender.isInsideVehicle()) {
            Entity vehicle = sender.getVehicle();
            // Cancel unmount if needed
            if (vehicle.isOnGround() || !UNMOUNT_FIELD.get(packet)) {
                vehicle.removeMetadata("MOUNT_DOWN", HubPlugin.get());
            } else {
                UNMOUNT_FIELD.set(packet, false);
                vehicle.setMetadata("MOUNT_DOWN", new FixedMetadataValue(HubPlugin.get(), true));
            }
        }
        return packet;
    }
}
