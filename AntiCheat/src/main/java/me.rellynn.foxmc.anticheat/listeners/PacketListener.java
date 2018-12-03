package me.rellynn.foxmc.anticheat.listeners;

import io.netty.channel.Channel;
import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.blocks.ScaffoldWalkCheck;
import me.rellynn.foxmc.anticheat.checks.combat.AutoClickerCheck;
import me.rellynn.foxmc.anticheat.checks.combat.ConsistencyCheck;
import me.rellynn.foxmc.anticheat.checks.combat.ReachCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.FightData;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.tasks.HandleVelocity;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.TinyProtocol;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class PacketListener extends TinyProtocol {
    /*
    Reflection
     */
    private static final Reflection.FieldAccessor<Integer> VELOCITY_EID = Reflection.getField(PacketPlayOutEntityVelocity.class, int.class, 0);
    private static final Reflection.FieldAccessor<Integer> VELOCITY_X = Reflection.getField(PacketPlayOutEntityVelocity.class, int.class, 1);
    private static final Reflection.FieldAccessor<Integer> VELOCITY_Y = Reflection.getField(PacketPlayOutEntityVelocity.class, int.class, 2);
    private static final Reflection.FieldAccessor<Integer> VELOCITY_Z = Reflection.getField(PacketPlayOutEntityVelocity.class, int.class, 3);
    /*
    Combat checks
     */
    private static final ReachCheck REACH_CHECK = new ReachCheck();
    private static final AutoClickerCheck AUTO_CLICKER_CHECK = new AutoClickerCheck();
    private static final ConsistencyCheck CONSISTENCY_CHECK = new ConsistencyCheck();
    /*
    Blocks checks
     */
    private static final ScaffoldWalkCheck SCAFFOLD_WALK_CHECK = new ScaffoldWalkCheck();

    public PacketListener() {
        super(ACPlugin.get());
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        if (packet instanceof PacketPlayOutSetSlot || packet instanceof PacketPlayOutWindowItems) {
            // Reset check for NoSlowDown hack
            ACPlayer.get(receiver).checkSlowdown = false;
        } else if (packet instanceof PacketPlayOutEntityVelocity) {
            int entityId = VELOCITY_EID.get(packet);
            if (entityId == receiver.getEntityId()) {
                new HandleVelocity(receiver, VELOCITY_X.get(packet), VELOCITY_Y.get(packet), VELOCITY_Z.get(packet));
            }
        }
        return super.onPacketOutAsync(receiver, channel, packet);
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        ACPlayer acPlayer = ACPlayer.get(sender);
        if (packet instanceof PacketPlayInClientCommand && ((PacketPlayInClientCommand) packet).a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            acPlayer.inventoryOpened = true;
        } else if (packet instanceof PacketPlayInTransaction) {
            acPlayer.handleTransaction((PacketPlayInTransaction) packet);
        } else if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
            acPlayer.checkSlowdown = false;
        } else if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            Entity entity = ((PacketPlayInUseEntity) packet).a(((CraftWorld) sender.getWorld()).getHandle());
            FightData fightData = acPlayer.fightData;
            if (entity != null && entity.isAlive()) {
                // Process Reach check at packet-level
                REACH_CHECK.check(sender, acPlayer, acPlayer.movingData, fightData, entity.getBukkitEntity());
            }
            long now = System.currentTimeMillis();
            if (fightData.lastAttackTime != 0L) {
                fightData.previousAttacks.add(now - fightData.lastAttackTime);
            }
            fightData.lastAttackTime = now;
            AUTO_CLICKER_CHECK.check(sender, acPlayer, fightData);
            CONSISTENCY_CHECK.check(sender, acPlayer, fightData);
        } else if (packet instanceof PacketPlayInFlying) {
            MovingData data = acPlayer.movingData;
            data.packetsCount++;
            data.clientOnGround = ((PacketPlayInFlying) packet).f();
            data.previousPackets.add((PacketPlayInFlying) packet);
            SCAFFOLD_WALK_CHECK.check(sender, acPlayer);
        }
        return super.onPacketInAsync(sender, channel, packet);
    }
}
