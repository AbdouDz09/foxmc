package me.rellynn.foxmc.bukkitapi.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
@Getter
public class ModerationTeleportEvent extends PlayerEvent {
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private Player target;

    public ModerationTeleportEvent(Player who, Player target) {
        super(who);
        this.target = target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
