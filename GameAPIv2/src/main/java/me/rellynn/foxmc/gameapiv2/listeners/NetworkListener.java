package me.rellynn.foxmc.gameapiv2.listeners;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.events.FriendTeleportEvent;
import me.rellynn.foxmc.bukkitapi.events.ModerationTeleportEvent;
import me.rellynn.foxmc.bukkitapi.events.PartyJoinMatchEvent;
import me.rellynn.foxmc.bukkitapi.events.PlayerJoinMatchEvent;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
public class NetworkListener implements Listener {
    private final FoxAPI foxAPI = FoxAPI.get();

    @EventHandler
    public void onFriendTeleport(FriendTeleportEvent evt) {
        Player player = evt.getPlayer();
        Game current = GameAPIv2.get().getGamesManager().whereIs(player);
        if (current != null) {
            current.leave(player, true);
        }
        Game friendGame = GameAPIv2.get().getGamesManager().whereIs(evt.getFriend());
        if (friendGame != null) {
            friendGame.join(player);
        }
    }

    @EventHandler
    public void onModerationTeleport(ModerationTeleportEvent evt) {
        Game current = GameAPIv2.get().getGamesManager().whereIs(evt.getPlayer());
        if (current != null) {
            current.leave(evt.getPlayer(), true);
        }
        Game target = GameAPIv2.get().getGamesManager().whereIs(evt.getTarget());
        if (target != null) {
            target.setSpectator(evt.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoinMatch(PlayerJoinMatchEvent evt) {
        Game game = GameAPIv2.get().getGamesManager().getGame(evt.getMatch().getId());
        if (game == null || game.getCurrentPhase() == null) {
            evt.setCancelReason("§cThe game is starting. Please wait...");
        } else if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers() && !foxAPI.getPlayersHandler().getPlayer(evt.getPlayer()).isAtLeast(Rank.VIP)) {
            evt.setCancelReason("§cServer is full! Buy " + Rank.VIP.getName() + " §cor higher to join!");
        }
    }

    @EventHandler
    public void onPartyJoinMatch(PartyJoinMatchEvent evt) {
        Game game = GameAPIv2.get().getGamesManager().getGame(evt.getMatch().getId());
        if (game == null || game.getCurrentPhase() == null) {
            evt.setCancelReason("§cThe game is starting. Please wait...");
        } else if ((game.getCurrentPhase().isJoinable() && game.getPlayers().size() + evt.getMembers().size() > game.getArena().getMaxPlayers())
                || (!game.getCurrentPhase().isJoinable() && Bukkit.getOnlinePlayers().size() + evt.getMembers().size() > Bukkit.getMaxPlayers())) {
            evt.setCancelReason("§cThere is not enough space for your party!");
        }
    }
}
