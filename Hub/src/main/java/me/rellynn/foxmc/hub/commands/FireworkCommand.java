package me.rellynn.foxmc.hub.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.bukkitapi.utils.FireworkUtils;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public class FireworkCommand extends PlayerCommand {
    private Cache<UUID, Long> lastUse = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public FireworkCommand() {
        super(Rank.VIP);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        long now = System.currentTimeMillis();
        long diff = System.currentTimeMillis() - lastUse.asMap().getOrDefault(player.getUniqueId(), 0L);
        if (diff < 5000L) {
            int seconds = (int) Math.ceil(5 - diff / (double) 1000);
            player.sendMessage("§cYou must wait §e" + seconds + " second" + (seconds > 1 ? "s" : "") + " §cto send a new firework!");
            return;
        }
        lastUse.put(player.getUniqueId(), now);
        FireworkUtils.spawnRandomFirework(player.getEyeLocation());
        player.sendMessage("§6Firework launched! §7Look above your head!");
    }
}
