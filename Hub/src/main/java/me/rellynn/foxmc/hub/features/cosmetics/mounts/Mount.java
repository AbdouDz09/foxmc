package me.rellynn.foxmc.hub.features.cosmetics.mounts;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.BaseCosmetic;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public abstract class Mount extends BaseCosmetic {

    public Mount(String id, Rank rank) {
        super("mounts", id, rank);
    }

    public Mount(String id) {
        this(id, Rank.DEFAULT);
    }

    @Override
    public void onUse(Player player) {
        player.sendMessage("§aMount spawned!");
        player.closeInventory();
    }
}

