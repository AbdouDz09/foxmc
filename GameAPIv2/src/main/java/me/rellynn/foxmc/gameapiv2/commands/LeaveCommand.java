package me.rellynn.foxmc.gameapiv2.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 03/06/2017.
 * FoxMC Network.
 */
public class LeaveCommand extends GameCommand {
    private Cache<UUID, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    @Override
    public void onCommand(Game game, Player player, String[] args) {
        Long last = cache.getIfPresent(player.getUniqueId());
        if (game.isState(GameState.ENDING) || !game.isPlayer(player) || (last != null && System.currentTimeMillis() - last < 5000)) {
            game.sendToHub(player);
        } else {
            cache.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage("§cAre you sure you want to quit the game? Type §e/leave §cagain to confirm.");
        }
    }
}
