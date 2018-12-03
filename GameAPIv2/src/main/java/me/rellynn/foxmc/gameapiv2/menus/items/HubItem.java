package me.rellynn.foxmc.gameapiv2.menus.items;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class HubItem extends VirtualItem {
    private static final ItemStack ITEM = new ItemBuilder(Material.BED)
            .setTitle("§cReturn to hub")
            .addLore("§7Right click to return to hub")
            .build();

    private Game game;
    private Cache<UUID, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public HubItem(Game game) {
        super(game.getMatch().getName() + "_hub", ActionType.CUSTOM, "");
        this.game = game;
    }

    @Override
    public void onClick(Player player, PerformedAction action) {
        if (action != PerformedAction.RIGHT_CLICK) {
            return;
        } else if (!cache.asMap().containsKey(player.getUniqueId())) {
            cache.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage("§cDo you really want to quit the game? Click §eagain §cto §aconfirm§c.");
            return;
        }
        game.sendToHub(player);
    }

    @Override
    public ItemStack getItem(Player player) {
        return ITEM;
    }
}
