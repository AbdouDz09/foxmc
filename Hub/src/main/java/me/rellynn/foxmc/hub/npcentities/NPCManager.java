package me.rellynn.foxmc.hub.npcentities;

import io.netty.channel.Channel;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.TinyProtocol;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubPlugin;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 26/05/2017.
 * FoxMC Network.
 */
public class NPCManager extends TinyProtocol implements Listener {
    private static final Reflection.FieldAccessor<Integer> useEntityId = Reflection.getField(PacketPlayInUseEntity.class, "a", int.class);

    private final HubPlugin plugin = HubPlugin.get();

    private File configFile;
    private YamlConfiguration config;
    private int nextId = 1;
    private Set<NPC> npcSet = new HashSet<>();

    public NPCManager() {
        super(HubPlugin.get());
        this.configFile = new File(plugin.getDataFolder(), "npc.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getCommand("npc").setExecutor(new NPCCommand());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void loadFromDisk() {
        config.getKeys(false).forEach(key -> {
            ConfigurationSection npcConfig = config.getConfigurationSection(key);
            int id = Integer.parseInt(key);
            if (nextId < id) {
                nextId = id + 1;
            }
            World world = Bukkit.getWorld(npcConfig.getString("location.world"));
            Location location = new Location(world, npcConfig.getDouble("location.x"), npcConfig.getDouble("location.y"), npcConfig.getDouble("location.z"), (float) npcConfig.getDouble("location.yaw"), (float) npcConfig.getDouble("location.pitch"));
            NPC npc = spawnNPC(id, EntityType.valueOf(npcConfig.getString("type")), location);
            if (npc == null) {
                plugin.getLogger().severe("Couldn't load the NPC " + id + "!");
            } else {
                // NPC name
                String name = npcConfig.getString("name");
                if (name != null) {
                    npc.setName(name);
                }
                // Load entity equipment
                ConfigurationSection equipment = npcConfig.getConfigurationSection("equipment");
                if (equipment != null) {
                    equipment.getKeys(false).forEach(slot -> npc.setEquipment(Integer.parseInt(slot), equipment.getItemStack(key)));
                }
                npc.setInteractAction(NPC.ClickAction.valueOf(npcConfig.getString("action")), npcConfig.getString("actionValue"));
                plugin.getLogger().info("NPC " + id + " loaded!");
            }
        });
    }

    public void saveToDisk() {
        npcSet.forEach(npc -> {
            String key = npc.getId() + ".";
            config.set(key + "type", npc.getType().name());
            config.set(key + "location.world", npc.getLocation().getWorld().getName());
            config.set(key + "location.x", npc.getLocation().getX());
            config.set(key + "location.y", npc.getLocation().getY());
            config.set(key + "location.z", npc.getLocation().getZ());
            config.set(key + "location.yaw", npc.getLocation().getYaw());
            config.set(key + "location.pitch", npc.getLocation().getPitch());
            config.set(key + "name", npc.getName());
            config.set(key + "actionType", npc.getActionType().name());
            config.set(key + "actionValue", npc.getActionValue());
            // Save equipment
            for (int i = 0; i < npc.getEntity().getEquipment().length; i++) {
                net.minecraft.server.v1_8_R3.ItemStack item = npc.getEntity().getEquipment()[i];
                if (item != null) {
                    config.set(key + "equipment." + i, CraftItemStack.asBukkitCopy(item));
                }
            }
            npc.destroy();
        });
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace(); // Should never happen?
        }
    }

    public NPC getNPC(int id) {
        return npcSet.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);
    }

    public NPC spawnNPC(int id, EntityType type, Location location) {
        if (id >= nextId) {
            nextId = id + 1;
        }
        EntityLiving entity = (EntityLiving) ((CraftWorld) location.getWorld()).createEntity(location, type.getEntityClass());
        entity.f(location.getYaw());
        entity.g(location.getYaw());
        NPC npc = new NPC(id, type, location, entity);
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) npc::spawn);
        npcSet.add(npc);
        return npc;
    }

    public NPC spawnNPC(EntityType type, Location location) {
        return spawnNPC(nextId, type, location);
    }

    public boolean removeNPC(int id) {
        NPC npc = getNPC(id);
        if (npc != null) {
            npc.destroy();
            npcSet.remove(npc);
            config.set(id + "", null);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace(); // Should never happen?
            }
            return true;
        }
        return false;
    }

    /*
    Events
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        npcSet.forEach(npc -> npc.spawn(evt.getPlayer()));
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if (!(packet instanceof PacketPlayInUseEntity) || ((PacketPlayInUseEntity) packet).a() != PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
            return packet;
        }
        NPC npc = getNPC(useEntityId.get(packet));
        if (npc != null) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    String value = npc.getActionValue();
                    switch (npc.getActionType()) {
                        case JOIN_SERVER:
                            FoxAPI.get().getNetworkHandler().sendPlayer(sender, value);
                            break;
                        case OPEN_MENU:
                            VirtualMenu.getMenu(value).open(sender);
                            break;
                        case MESSAGE:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', value));
                            break;
                    }
                }
            }.runTask(plugin);
            return null;
        }
        return packet;
    }
}
