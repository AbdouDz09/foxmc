package me.rellynn.foxmc.treewars.listeners;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.utils.EventUtils;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.game.TWGame;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.phases.RunningPhase;
import me.rellynn.foxmc.treewars.scheduler.RespawnTask;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class EntityListener implements Listener {
    private TWGame game = TWPlugin.getGame();

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        evt.blockList().removeIf(block -> !block.hasMetadata("PLACED_BLOCK"));
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent evt) {
        if (evt.getEntity().getVehicle() instanceof ArmorStand) {
            evt.setCancelled(true);
            evt.getEntity().setTicksLived(1); // Reset lived ticks
        }
    }

    private boolean hasSpawnProtection(Player player) {
        return player.hasMetadata("SPAWN_PROTECTION") && System.currentTimeMillis() - player.getMetadata("SPAWN_PROTECTION").get(0).asLong() < 10000;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Player damager = EventUtils.getDamager(evt);
            if (damager != null) {
                if (hasSpawnProtection(player) || game.getArena().getPlayerTeam(player) == game.getArena().getPlayerTeam(damager)) {
                    evt.setCancelled(true);
                } else if (hasSpawnProtection(damager)) {
                    damager.removeMetadata("SPAWN_PROTECTION", TWPlugin.get());
                    damager.sendMessage("§eYou lost your spawn protection!");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity().hasMetadata("OPEN_MENU")) {
            evt.setCancelled(true);
            Player damager = EventUtils.getDamager(evt);
            if (damager != null) {
                damager.playSound(damager.getEyeLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                damager.sendMessage("§cPlease don't kill me! :(");
                game.increaseStatistic(damager, "npckills_tries");
            }
        } else if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            boolean isVoid = evt.getCause() == EntityDamageEvent.DamageCause.VOID;
            if ((isVoid || player.getHealth() - evt.getFinalDamage() <= 0) && game.getArena().hasTeam(player)) {
                evt.setCancelled(true);
                Player killer = player.getKiller();
                if (killer == null) {
                    killer = EventUtils.getDamager(evt); // Try to get the killer from the current damage event
                }
                String deathMsg = game.getDisplayName(player) + " §7";
                Team team = game.getArena().getPlayerTeam(player);
                boolean isFinalKill = game.isPhase(RunningPhase.class) && game.getTree(team).isDead();
                if (killer == null) {
                    deathMsg += isVoid ? "fell into the void" : "was killed";
                } else {
                    deathMsg += (isVoid ? "was knocked into the void" : "was killed") + " by " + game.getDisplayName(killer);
                    killer.playSound(killer.getEyeLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    game.increaseStatistic(killer, "kills");
                    if (isFinalKill) {
                        game.increaseStatistic(killer, "final_kills");
                        game.giveCoins(killer, 3.0F, "final kill");
                        game.giveTails(killer, 1.25F, "kill bonus");
                    }
                    // Give player currencies to the killer
                    for (TWCurrency currency : TWCurrency.values()) {
                        int totalCurrency = 0;
                        for (ItemStack item : player.getInventory().getContents()) {
                            if (currency.getItem().isSimilar(item)) {
                                killer.getInventory().addItem(item);
                                totalCurrency += item.getAmount();
                            }
                        }
                        if (totalCurrency != 0)
                            killer.sendMessage(currency.getPrefix() + "+" + totalCurrency + " " + currency.getName());
                    }
                }
                deathMsg += "§7.";
                PlayerUtils.resetPlayer(player);
                game.increaseStatistic(player, "deaths");
                if (!isFinalKill) {
                    new RespawnTask(player, game.getTree(team).hasUpgrade(Upgrade.FAST_RESPAWN) ? 3 : 5);
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    deathMsg += " §6§lFINAL KILL";
                    team.leave(game, player);
                    player.sendMessage("§cYou have been eliminated!");
                    PacketUtils.sendTitle("§cYOU LOST!", player);
                    PacketUtils.sendSubTitle("You have been eliminated!", player);
                    game.setSpectator(player);
                }
                game.broadcast(deathMsg);
                player.teleport(game.getArena().getPoint("lobby"));
            }
        }
    }
}
