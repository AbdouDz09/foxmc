package me.rellynn.foxmc.hub.features.cosmetics.gadgets;

import lombok.Getter;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.BaseCosmetic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 19/05/2017.
 * FoxMC Network.
 */
public abstract class Gadget extends BaseCosmetic {
    private int cooldown;
    @Getter private int duration;
    private Map<UUID, Integer> cooldowns = new HashMap<>();
    private Map<UUID, BukkitTask> tasks = new HashMap<>();

    protected Gadget(String id, Rank rank, int cooldown, int duration) {
        super("gadgets", id, rank);
        this.cooldown = cooldown;
        this.duration = duration;
    }

    protected Gadget(String id, int cooldown, int duration) {
        this(id, Rank.DEFAULT, cooldown, duration);
    }

    void enable(Player player) {
        onStart(player);
        BukkitTask task = new BukkitRunnable() {
            private int ticksLeft = duration * 20;

            @Override
            public void run() {
                try {
                    onTick(player, ticksLeft);
                } catch (Exception ex) {
                    HubPlugin.get().getLogger().severe("Error while ticking a gadget:");
                    ex.printStackTrace();
                }
                if (!player.isValid() || (duration >= 0 && ticksLeft <= 0)) {
                    HubAPI.get().getGadgetsManager().removeGadget(player);
                    return;
                }
                ticksLeft--;
            }
        }.runTaskTimer(HubPlugin.get(), 0L, 1L);
        tasks.put(player.getUniqueId(), task);
    }

    void disable(Player player) {
        BukkitTask task = tasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            onEnd(player);
        }
    }

    @Override
    public void onUse(Player player) {
        setItem(player, player.getInventory(), 3);
        player.getInventory().setHeldItemSlot(3);
        player.closeInventory();
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        if (action == PerformedAction.DROP) {
            player.getInventory().setItemInHand(null);
            player.updateInventory();
            return;
        } else if (action == PerformedAction.LEFT_CLICK || action == PerformedAction.RIGHT_CLICK) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                PacketUtils.sendActionBar("§cGadget is reloading!!", player);
                return;
            }
            cooldowns.put(player.getUniqueId(), cooldown);
            new BukkitRunnable() {

                @Override
                public void run() {
                    int timeLeft = cooldowns.get(player.getUniqueId());
                    if (timeLeft > 0 && player.isValid()) {
                        cooldowns.put(player.getUniqueId(), timeLeft - 1);
                    } else {
                        cancel();
                        cooldowns.remove(player.getUniqueId());
                    }
                    VirtualMenu.getMenu("gadgets").updateOpened(player);
                    if (isThis(player.getInventory().getItem(3))) {
                        setItem(player, player.getInventory(), 3);
                        player.updateInventory();
                    }
                }
            }.runTaskTimer(HubPlugin.get(), 0L, 20L);
            HubAPI.get().getGadgetsManager().setGadget(player, this);
            return;
        }
        super.onClick(player, action);
    }

    @Override
    public ItemStack getItem(Player player) {
        ItemStack item = super.getItem(player);
        int cooldown = cooldowns.getOrDefault(player.getUniqueId(), 0);
        if (cooldown > 0) {
            item.setAmount(-cooldown);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(meta.getDisplayName() + " §7(§b" + cooldown + " §7second" + (cooldown != 1 ? "s" : "") + ")");
            item.setItemMeta(meta);
        }
        return item;
    }

    /*
    Hooks
     */
    public void onStart(Player player) {}

    public void onTick(Player player, int ticksLeft) {}

    public void onEnd(Player player) {}
}
