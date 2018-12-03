package me.rellynn.foxmc.gameapiv2.games;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class GamesManager {
    private Map<Integer, Game> games = new ConcurrentHashMap<>();

    public void unregisterAll() {
        games.values().forEach(this::removeGame);
    }

    void storeGame(Game game) {
        game.register();
        games.put(game.getMatch().getId(), game);
    }

    void removeGame(Game game) {
        games.remove(game.getMatch().getId());
        game.unregister();
    }

    public Game getGame(int id) {
        return games.get(id);
    }

    public Game whereIs(Player player) {
        return games.values().stream().filter(game -> game.contains(player)).findFirst().orElse(null);
    }
}
