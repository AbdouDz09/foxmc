package me.rellynn.foxmc.hub.features.cosmetics.gadgets.entities;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.World;

/**
 * Created by gwennaelguich on 24/06/2017.
 * FoxMC Network.
 */
public class InstantFireworksEntity extends EntityFireworks {

    public InstantFireworksEntity(World world) {
        super(world);
        this.a(0.25F, 0.25F);
    }

    @Override
    public void t_() {
        this.world.broadcastEntityEffect(this, (byte) 17);
        this.die();
    }
}
