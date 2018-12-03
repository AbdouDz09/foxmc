package me.rellynn.foxmc.hub.features.cosmetics.mounts.entities;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Horse;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public class EntityHorseMount extends EntityHorse {

    public EntityHorseMount(World world) {
        super(world);
        this.inventoryChest.setItem(0, new ItemStack(Items.SADDLE));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2D);
    }

    @Override
    public void g(float sideMot, float forMot) {
        if (!(this.passenger instanceof EntityHuman)) {
            die();
        } else {
            super.g(sideMot, forMot);
        }
    }

    @Override
    public void t_() {
        super.t_();
        // Particles
        if (getType() == Horse.Variant.SKELETON_HORSE.ordinal()) {
            Location location = getBukkitEntity().getLocation().add(0.0D, 1.0D, 0.0D);
            location.getWorld().spigot().playEffect(location, Effect.FLAME, 0, 0, 0.4F, 0.2F, 0.4F, 0.0F, 1, 16);
        }
    }

    /*
    Prevent horse inventory opening
     */
    @Override
    public void g(EntityHuman entityhuman) {}

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
