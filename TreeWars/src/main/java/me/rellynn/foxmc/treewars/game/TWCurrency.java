package me.rellynn.foxmc.treewars.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum TWCurrency {
    SEEDS("§a", "Seeds", new ItemBuilder(Material.SEEDS).setTitle("§aSeeds").build()),
    COCOA("§6", "Cocoa", new ItemBuilder(Material.INK_SACK, 1, (short) 3).setTitle("§6Cocoa").build()),
    BLUE_ORCHID("§b", "Blue Orchid", new ItemBuilder(Material.RED_ROSE, 1, (short) 1).setTitle("§bBlue Orchid").build()),
    DEAD_BUSH("§f", "Dead Bush", new ItemBuilder(Material.DEAD_BUSH).setGlow(true).build());

    private String prefix;
    private String name;
    private ItemStack item;

    public String format(int amount) {
        return String.format("%s%d %s", prefix, amount, name);
    }

    public boolean takeItems(Player player, int cost) {
        if (!player.getInventory().containsAtLeast(item, cost)) {
            return false;
        }
        int toRemove = cost;
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (this.item.isSimilar(item)) {
                if (item.getAmount() > toRemove) {
                    item.setAmount(item.getAmount() - toRemove);
                    break;
                } else {
                    toRemove -= item.getAmount();
                    player.getInventory().clear(i);
                }
            }
        }
        return true;
    }
}
