package me.rellynn.foxmc.hub.features.cosmetics.gadgets;

import me.rellynn.foxmc.hub.HubPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 23/05/2017.
 * FoxMC Network.
 */
public class GadgetsManager implements Listener {
    private Map<Player, Gadget> playerGadgets = new HashMap<>();

    public GadgetsManager() {
        Bukkit.getPluginManager().registerEvents(this, HubPlugin.get());
    }

    public void cancelGadgets() {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) this::removeGadget);
    }

    public Gadget getGadget(Player player) {
        return playerGadgets.get(player);
    }

    public boolean removeGadget(Player player) {
        Gadget gadget = playerGadgets.remove(player);
        if (gadget != null) {
            gadget.disable(player);
            return true;
        }
        return false;
    }

    public void setGadget(Player player, Gadget gadget) {
        removeGadget(player);
        playerGadgets.put(player, gadget);
        gadget.enable(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        removeGadget(evt.getPlayer());
    }
}
