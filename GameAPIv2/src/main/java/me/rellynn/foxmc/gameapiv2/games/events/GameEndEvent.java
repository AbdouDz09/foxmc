package me.rellynn.foxmc.gameapiv2.games.events;

import me.rellynn.foxmc.gameapiv2.games.Game;


/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class GameEndEvent<T extends Game> extends GameEvent<T> {

    public GameEndEvent(T game) {
        super(game);
    }
}
