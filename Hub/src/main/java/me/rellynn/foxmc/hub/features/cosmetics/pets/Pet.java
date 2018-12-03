package me.rellynn.foxmc.hub.features.cosmetics.pets;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.BaseCosmetic;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
public abstract class Pet extends BaseCosmetic {
    private Map<UUID, List<Entity>> entities = new HashMap<>();

    public Pet(String id, Rank rank) {
        super("pets", id, rank);
        HubAPI.get().getPetsManager().registerPet(this);
    }

    public Pet(String id) {
        this(id, Rank.DEFAULT);
    }

    protected abstract List<Entity> buildEntities(Player player);

    public List<Entity> createPet(Player player) {
        List<Entity> builtEntities = buildEntities(player);
        entities.put(player.getUniqueId(), builtEntities);
        builtEntities.forEach(entity -> {
            if (entity instanceof EntityInsentient) {
                ((EntityInsentient) entity).k(true);
            }
        });
        return builtEntities;
    }

    public List<Entity> getEntities(Player player) {
        return entities.get(player.getUniqueId());
    }

    public List<Entity> destroyEntities(Player player) {
        List<Entity> entities = this.entities.remove(player.getUniqueId());
        if (entities != null) {
            entities.forEach(Entity::die);
        }
        return entities;
    }

    @Override
    public void onUse(Player player) {
        player.sendMessage("§aPet spawned!");
        player.closeInventory();
        HubAPI.get().getPetsManager().setPet(player, this);
    }
}
