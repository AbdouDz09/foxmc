package me.rellynn.foxmc.hub.visibility;

import io.netty.channel.Channel;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.TinyProtocol;
import me.rellynn.foxmc.hub.HubPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 25/05/2017.
 * FoxMC Network.
 */
public class VisibilityManager {
    // Packets to cancel when the visibility is disabled
    private static final List<Class> WORLD_PACKETS = Arrays.asList(new Class[]{
            PacketPlayOutNamedSoundEffect.class,
            PacketPlayOutWorldEvent.class,
            PacketPlayOutWorldParticles.class,
            PacketPlayOutExplosion.class
    });
    private static final List<Class> ENTITY_PACKETS = Arrays.asList(new Class[]{
            PacketPlayOutEntityEquipment.class, PacketPlayOutBed.class, PacketPlayOutAnimation.class, PacketPlayOutNamedEntitySpawn.class,
            PacketPlayOutCollect.class, PacketPlayOutSpawnEntity.class, PacketPlayOutSpawnEntityLiving.class, PacketPlayOutSpawnEntityExperienceOrb.class,
            PacketPlayOutEntityVelocity.class, PacketPlayOutEntity.PacketPlayOutRelEntityMove.class, PacketPlayOutEntity.PacketPlayOutEntityLook.class, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class,
            PacketPlayOutEntityTeleport.class, PacketPlayOutEntityHeadRotation.class, PacketPlayOutEntityStatus.class, PacketPlayOutAttachEntity.class, PacketPlayOutEntityMetadata.class,
            PacketPlayOutEntityEffect.class, PacketPlayOutRemoveEntityEffect.class, PacketPlayOutBlockBreakAnimation.class
    });

    private Map<Integer, Boolean> states = new ConcurrentHashMap<>();

    public VisibilityManager() {
        Bukkit.getPluginManager().registerEvents(new VisibilityListener(), HubPlugin.get());
    }

    private void showEntity(Player player, int entityId) {
        EntityTrackerEntry entry = ((CraftWorld) player.getWorld()).getHandle().tracker.trackedEntities.get(entityId);
        if (entry != null) {
            if (entry.tracker instanceof EntityPlayer) {
                player.showPlayer(((EntityPlayer) entry.tracker).getBukkitEntity());
                return;
            }
            entry.updatePlayer(((CraftPlayer) player).getHandle());
        }
    }

    private void hideEntity(Player player, int entityId) {
        EntityTrackerEntry entry = ((CraftWorld) player.getWorld()).getHandle().tracker.trackedEntities.get(entityId);
        if (entry != null) {
            if (entry.tracker instanceof EntityPlayer) {
                player.hidePlayer(((EntityPlayer) entry.tracker).getBukkitEntity());
                return;
            }
            entry.clear(((CraftPlayer) player).getHandle());
        }
    }

    private boolean shouldBeVisible(int entityId) {
        return states.getOrDefault(entityId, false);
    }

    private boolean shouldBeInvisible(int entityId) {
        return !states.getOrDefault(entityId, true);
    }

    public void setVisible(org.bukkit.entity.Entity entity) {
        states.put(entity.getEntityId(), true);
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::updateVisibility);
    }

    public void setInvisible(org.bukkit.entity.Entity entity) {
        states.put(entity.getEntityId(), false);
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::updateVisibility);
    }

    public void resetState(org.bukkit.entity.Entity entity) {
        if (entity instanceof Player && FoxAPI.getPlayer((Player) entity).getModLevel() > 0) {
            setVisible(entity);
        } else {
            states.remove(entity.getEntityId());
            Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::updateVisibility);
        }
    }

    public void updateVisibility(Player player) {
        boolean isEnabled = Settings.hubVisibility.is(FoxAPI.getPlayer(player), SettingValue.ENABLED);
        player.getWorld().getEntities().forEach(entity -> {
            int entityId = entity.getEntityId();
            if (entityId != player.getEntityId()) {
                if ((isEnabled && shouldBeInvisible(entityId)) || (!isEnabled && !shouldBeVisible(entityId))) {
                    hideEntity(player, entityId);
                } else {
                    showEntity(player, entityId);
                }
            }
        });
    }

    /*
    Listener
     */
    private class VisibilityListener extends TinyProtocol implements Listener {

        VisibilityListener() {
            super(HubPlugin.get());
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent evt) {
            updateVisibility(evt.getPlayer());
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent evt) {
            states.remove(evt.getPlayer().getEntityId());
        }

        @Override
        public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
            boolean isEnabled = Settings.hubVisibility.is(FoxAPI.getPlayer(receiver), SettingValue.ENABLED);
            if (!isEnabled && WORLD_PACKETS.contains(packet.getClass())) {
                return null;
            } else if (ENTITY_PACKETS.contains(packet.getClass())) {
                int entityId = Reflection.getField(packet.getClass(), int.class, 0).get(packet);
                if (entityId == receiver.getEntityId()) {
                    return packet;
                } else if ((isEnabled && shouldBeInvisible(entityId)) || (!isEnabled && !shouldBeVisible(entityId))) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            hideEntity(receiver, entityId);
                        }
                    }.runTask(HubPlugin.get());
                    return null;
                }
            }
            return packet;
        }
    }
}
