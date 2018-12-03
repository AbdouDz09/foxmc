package me.rellynn.foxmc.gameapiv2.games.phases;

import me.rellynn.foxmc.gameapiv2.games.GameState;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class EndingPhase extends Phase {

    public EndingPhase(int seconds) {
        super(GameState.ENDING);
        setDelay(seconds * 20L);
    }

    public EndingPhase() {
        this(10);
    }

    @Override
    protected void onTick() {
        game.getAllPlayers().forEach(player -> game.sendToHub(player));
    }
}
