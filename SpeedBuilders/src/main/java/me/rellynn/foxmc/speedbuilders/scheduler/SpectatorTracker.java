package me.rellynn.foxmc.speedbuilders.scheduler;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 05/07/2017.
 * FoxMC Network.
 */
public class SpectatorTracker extends BukkitRunnable {
    private final SBGame game = SBPlugin.getGame();

    private Player tracker;
    private Set<Player> trackedPlayers = new HashSet<>();
    private int xLoc;
    private int yLoc;
    private int zLoc;
    private int yRot;
    private int xRot;
    private boolean onGround;
    private int counter;

    public SpectatorTracker(Player player) {
        this.tracker = player;
        Location location = player.getLocation();
        this.xLoc = (int) Math.floor(location.getX() * 32.0D);
        this.yLoc = (int) Math.floor(location.getY() * 32.0D);
        this.zLoc = (int) Math.floor(location.getZ() * 32.0D);
        this.yRot = (int) Math.floor(location.getYaw() * 256.0F / 360.0F);
        this.xRot = (int) Math.floor(location.getPitch() * 256.0F / 360.0F);
        runTaskTimer(SBPlugin.get(), 0L, 2L);
    }

    private void scanPlayers() {
        Location location = tracker.getLocation();
        Platform<Player> platform = game.getPlatform(location);
        if (platform != null || tracker.getSpectatorTarget() != null) {
            trackedPlayers.forEach(player -> PacketUtils.sendPacket(player, new PacketPlayOutEntityDestroy(tracker.getEntityId())));
            trackedPlayers.clear();
        } else {
            game.getAllPlayers().forEach(player -> {
                if (player != tracker && game.isPlayer(player)) {
                    Location playerLoc = player.getLocation();
                    double xDiff = playerLoc.getX() - location.getX();
                    double zDiff = playerLoc.getZ() - location.getZ();
                    if (Math.abs(xDiff) > 512.0D || Math.abs(zDiff) > 512.0D) {
                        trackedPlayers.remove(player);
                    } else if (trackedPlayers.add(player)) {
                        PacketUtils.sendPacket(player, game.getMorphPacket(tracker));
                    }
                }
            });
            // Remove old entries
            trackedPlayers.removeIf(player -> !player.isOnline() || !game.isPlayer(player));
        }
    }

    @Override
    public void run() {
        Packet packet;
        if (!tracker.isOnline() || !game.isSpectator(tracker)) {
            packet = new PacketPlayOutEntityDestroy(tracker.getEntityId());
            cancel();
        } else {
            counter++;
            scanPlayers();
            Location location = tracker.getLocation();
            int x = (int) Math.floor(location.getX() * 32.0D);
            int y = (int) Math.floor(location.getY() * 32.0D);
            int z = (int) Math.floor(location.getZ() * 32.0D);
            int yaw = (int) Math.floor(location.getYaw() * 256.0F / 360.0F);
            int pitch = (int) Math.floor(location.getPitch() * 256.0F / 360.0F);
            int xRel = x - xLoc;
            int yRel = y - yLoc;
            int zRel = z - zLoc;
            boolean hasMoved = Math.abs(xRel) >= 4 || Math.abs(yRel) >= 4 || Math.abs(zRel) >= 4;
            boolean hasRotated = Math.abs(yaw - yRot) >= 4 || Math.abs(pitch - xRot) >= 4;
            if (hasMoved) {
                xLoc = x;
                yLoc = y;
                zLoc = z;
            }
            if (hasRotated) {
                yRot = yaw;
                xRot = pitch;
            }
            if (xRel >= -128 && xRel < 128 && yRel >= -128 && yRel < 128 && zRel >= -128 && zRel < 128 && counter < 400 && onGround == tracker.isOnGround()) {
                if (hasMoved && hasRotated) {
                    packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(tracker.getEntityId(), (byte) xRel, (byte) yRel, (byte) zRel, (byte) yaw, (byte) pitch, onGround);
                } else if (hasMoved) {
                    packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(tracker.getEntityId(), (byte) xRel, (byte) yRel, (byte) zRel, onGround);
                } else {
                    packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(tracker.getEntityId(), (byte) yaw, (byte) pitch, onGround);
                }
            } else {
                counter = 0;
                onGround = tracker.isOnGround();
                packet = new PacketPlayOutEntityTeleport(tracker.getEntityId(), x, y, z, (byte) yaw, (byte) pitch, onGround);
            }
        }
        trackedPlayers.forEach(player -> PacketUtils.sendPacket(player, packet));
    }
}
