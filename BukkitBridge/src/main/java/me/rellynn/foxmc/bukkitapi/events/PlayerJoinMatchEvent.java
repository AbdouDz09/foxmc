package me.rellynn.foxmc.bukkitapi.events;

import lombok.Getter;
import lombok.Setter;
import me.rellynn.foxmc.api.matches.MatchData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
@Getter
public class PlayerJoinMatchEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private UUID player;
    private MatchData match;
    @Setter private String cancelReason;

    public PlayerJoinMatchEvent(UUID player, MatchData match) {
        this.player = player;
        this.match = match;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
