package me.rellynn.foxmc.hub.features.cosmetics.effects;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.BaseCosmetic;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public abstract class Effect extends BaseCosmetic {
    private long period;
    private Map<UUID, BukkitTask> tasks = new HashMap<>();
    private Map<UUID, Integer> cursors = new HashMap<>();
    private List<Location> locations;

    public Effect(String shopItemId, long period, Rank rank) {
        super("effects", shopItemId, rank);
        this.period = period;
    }

    public Effect(String shopItemId, long period) {
        this(shopItemId, period, Rank.DEFAULT);
    }

    protected abstract List<Location> buildLocations();

    @Override
    public void onUse(Player player) {
        HubAPI.get().getEffectsManager().setEffect(player, this);
        player.sendMessage("§aEffect enabled!");
        player.closeInventory();
    }

    void enable(Player player) {
        onEnable(player);
        // Here we go, start the task!
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isValid()) {
                    HubAPI.get().getEffectsManager().stopEffect(player);
                    return;
                }
                try {
                    onTick(player);
                } catch (Exception ex) {
                    HubPlugin.get().getLogger().severe("Error while ticking an effect:");
                    ex.printStackTrace();
                }
            }
        }.runTaskTimer(HubPlugin.get(), 0, period);
        tasks.put(player.getUniqueId(), task);
        cursors.put(player.getUniqueId(), 0);
    }

    void disable(Player player) {
        BukkitTask task = tasks.remove(player.getUniqueId());
        if (task != null) {
            // Cancel task
            cursors.remove(player.getUniqueId());
            task.cancel();
            onDisable(player);
        }
    }

    public Location getLocation(Player player) {
        if (locations == null) {
            locations = buildLocations();
        }
        Location location = locations.get(cursors.compute(player.getUniqueId(), (uuid, cursor) -> {
            int nextCursor = cursor + 1;
            if (nextCursor == locations.size()) {
                return 0;
            }
            return nextCursor;
        }));
        return player.getLocation().add(location.getX(), location.getY(), location.getZ());
    }

    /*
    HOOKS
     */
    protected void onEnable(Player player) {}

    protected void onTick(Player player) {}

    protected void onDisable(Player player) {}
}
