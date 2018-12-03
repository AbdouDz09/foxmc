package me.rellynn.foxmc.gameapiv2.games.events;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.entity.Player;


/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
@Getter
public class PlayerLeaveGameEvent<T extends Game> extends GameEvent<T> {
    private Player player;

    public PlayerLeaveGameEvent(T game, Player player) {
        super(game);
        this.player = player;
    }
}
