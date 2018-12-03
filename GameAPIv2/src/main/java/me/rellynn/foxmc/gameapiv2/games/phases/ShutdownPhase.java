package me.rellynn.foxmc.gameapiv2.games.phases;

import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.bukkit.Bukkit;

/**
 * Created by gwennaelguich on 03/06/2017.
 * FoxMC Network.
 */
public class ShutdownPhase extends Phase {

    public ShutdownPhase() {
        super(GameState.ENDING);
        setDelay(100L);
    }

    @Override
    protected void onEnd() {
        Bukkit.shutdown();
    }
}
