package me.rellynn.foxmc.gameapiv2.games.events;

import me.rellynn.foxmc.gameapiv2.games.Game;

/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class GameStartEvent<T extends Game> extends GameEvent<T> {

    public GameStartEvent(T game) {
        super(game);
    }
}
