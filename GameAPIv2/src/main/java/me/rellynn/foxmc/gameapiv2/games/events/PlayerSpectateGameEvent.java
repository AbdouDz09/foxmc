package me.rellynn.foxmc.gameapiv2.games.events;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.entity.Player;


/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class PlayerSpectateGameEvent<T extends Game> extends GameEvent<T> {
    @Getter private Player player;

    public PlayerSpectateGameEvent(T game, Player player) {
        super(game);
        this.player = player;
    }
}
