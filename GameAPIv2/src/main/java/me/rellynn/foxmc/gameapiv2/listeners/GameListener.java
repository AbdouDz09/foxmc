package me.rellynn.foxmc.gameapiv2.listeners;

import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.utils.Cuboid;
import me.rellynn.foxmc.gameapiv2.utils.EventUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class GameListener implements Listener {
    private final GameAPIv2 gameAPI = GameAPIv2.get();

    private boolean shouldCancel(Player player, boolean bypass) {
        if (bypass && player.isOp()) { // OP Bypass
            return false;
        }
        Game game = gameAPI.getGamesManager().whereIs(player);
        return game == null || !game.isState(GameState.RUNNING) || !game.isPlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Game game = gameAPI.getGamesManager().whereIs(evt.getPlayer());
        if (game != null && !game.isSpectator(evt.getPlayer())) {
            game.join(evt.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        Game game = gameAPI.getGamesManager().whereIs(evt.getPlayer());
        if (game != null) {
            game.leave(evt.getPlayer(), true);
        }
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent evt) {
        Game game = gameAPI.getGamesManager().whereIs(evt.getPlayer());
        if (game != null && game.isState(GameState.WAITING)) {
            evt.setSpawnLocation(game.getArena().getPoint("lobby"));
        }
    }

    /*
    Arena borders
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (EventUtils.hasMoved(evt)) {
            Game game = gameAPI.getGamesManager().whereIs(evt.getPlayer());
            if (game != null) {
                Cuboid cuboid = game.getArena().getCuboid();
                boolean isWaiting = game.isState(GameState.WAITING);
                if ((evt.getTo().getY() < 0 && isWaiting) || (cuboid != null && !cuboid.contains(evt.getTo()))) {
                    evt.setCancelled(true);
                    if (!isWaiting && game.isPlayer(evt.getPlayer())) {
                        evt.getPlayer().sendMessage("§cYou can't leave the arena!");
                    } else {
                        evt.getPlayer().teleport(game.getArena().getPoint("lobby"));
                    }
                }
            }
        }
    }

    /*
    Blocks
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt) {
        if (shouldCancel(evt.getPlayer(), true)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        if (shouldCancel(evt.getPlayer(), true)) {
            evt.setCancelled(true);
        }
    }

    /*
    Players
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (shouldCancel(evt.getPlayer(), true)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent evt) {
        if (shouldCancel(evt.getPlayer(), true)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        if (shouldCancel(evt.getPlayer(), true)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
        Game game = gameAPI.getGamesManager().whereIs(evt.getPlayer());
        if (game == null) {
            evt.getRecipients().removeIf(player -> player != evt.getPlayer());
        } else {
            boolean isSpec = game.isSpectator(evt.getPlayer());
            evt.getRecipients().removeIf(player -> !game.contains(player) || (isSpec && !game.isSpectator(player) && !game.isState(GameState.ENDING)));
            if (isSpec) {
                evt.setFormat("§8[SPEC] §r" + evt.getFormat());
            } else if (game.isUsingTeams() && !game.getArena().getMode().equals("solo") && !game.isState(GameState.WAITING) && game.getArena().hasTeam(evt.getPlayer())) {
                if (!evt.getMessage().startsWith("!")) {
                    Team team = game.getArena().getPlayerTeam(evt.getPlayer());
                    evt.getRecipients().removeIf(player -> !team.hasPlayer(player));
                    evt.setFormat("§e[TEAM] §r" + evt.getFormat());
                } else {
                    String message = evt.getMessage().replaceFirst("!", "");
                    if (!message.isEmpty())
                        evt.setMessage(message);
                }
            }
        }
    }

    /*
    Entities
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player && shouldCancel((Player) evt.getEntity(), false)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player && shouldCancel((Player) evt.getEntity(), false)) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player && shouldCancel((Player) evt.getDamager(), true)) {
            evt.setCancelled(true);
        }
    }
}
