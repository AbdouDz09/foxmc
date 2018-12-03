package me.rellynn.foxmc.bukkitapi.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class FriendTeleportEvent extends PlayerEvent {
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter private Player friend;

    public FriendTeleportEvent(Player who, Player friend) {
        super(who);
        this.friend = friend;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
