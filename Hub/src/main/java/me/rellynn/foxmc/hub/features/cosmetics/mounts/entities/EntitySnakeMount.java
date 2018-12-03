package me.rellynn.foxmc.hub.features.cosmetics.mounts.entities;

import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class EntitySnakeMount extends EntityMagmaCube {
    private static final Reflection.FieldAccessor<Boolean> JUMP_FIELD = Reflection.getField(EntityLiving.class, "aY", boolean.class);
    private List<ArmorStand> tailEntities = new ArrayList<>();

    public EntitySnakeMount(World world) {
        super(world);
        setSize(2);
    }

    public void summonTail(int tailSize) {
        Location location = getBukkitEntity().getLocation();
        Vector vector = location.getDirection().multiply(-0.25D);
        for (int i = 0; i < tailSize; i++) {
            ArmorStand armorStand = location.getWorld().spawn(location.add(vector), ArmorStand.class);
            tailEntities.add(armorStand);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setHelmet(new ItemStack(Material.NETHERRACK));
        }
    }

    private void updateTailPosition() {
        Location lastLocation = getBukkitEntity().getLocation();
        for (int i = 0; i < tailEntities.size(); i++) {
            ArmorStand armorStand = tailEntities.get(i);
            Location location = armorStand.getLocation();
            if (i != 0) {
                armorStand.teleport(lastLocation);
            } else {
                armorStand.teleport(lastLocation.clone().add(0, -1.3D, 0));
                armorStand.setHelmet(null);
            }
            armorStand.setHeadPose(new EulerAngle(Math.toRadians(lastPitch), Math.toRadians(lastYaw), 0));
            lastLocation = location;
        }
    }

    @Override
    public void die() {
        super.die();
        tailEntities.forEach(Entity::remove);
    }

    @Override
    public void g(float sideMot, float forMot) {
        if (!(this.passenger instanceof EntityHuman)) {
            die();
        } else {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aK = this.aI = this.yaw;
            this.S = 1.0F;
            sideMot = ((EntityLiving) this.passenger).aZ * 0.5F;
            forMot = ((EntityLiving) this.passenger).ba;
            if (forMot <= 0.0F) {
                forMot *= 0.25F;
            }
            sideMot *= 0.75F;
            this.k(0.2F);
            this.onGround = true; // Fake onGround
            super.g(sideMot, forMot);
            updateTailPosition();
            // Y (up/down)
            boolean isJumping = JUMP_FIELD.get(this.passenger);
            boolean isSneaking = this.getBukkitEntity().hasMetadata("MOUNT_DOWN");
            if (isSneaking && !isJumping) {
                this.motY = -0.5D;
            } else if (isJumping && !isSneaking) {
                this.motY = 0.5D;
            } else {
                this.motY = 0.0D;
            }
        }
    }

    /*
    Mounts should not be stored to wold save files
     */
    @Override
    public void b(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean c(NBTTagCompound nbttagcompound) { return false; }

    @Override
    public void a(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean d(NBTTagCompound nbttagcompound) { return false; }

    @Override
    public void e(NBTTagCompound nbttagcompound) {}
}
