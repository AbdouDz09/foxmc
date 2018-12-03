package me.rellynn.foxmc.gameapiv2.games.events;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class GameEvent<T extends Game> extends Event {
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter private T game;

    public GameEvent(T game) {
        this.game = game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
