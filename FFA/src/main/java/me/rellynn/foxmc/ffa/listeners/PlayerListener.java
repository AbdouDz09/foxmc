package me.rellynn.foxmc.ffa.listeners;

import com.google.gson.JsonElement;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.bukkitapi.utils.Scoreboard;
import me.rellynn.foxmc.ffa.FFAGame;
import me.rellynn.foxmc.ffa.FFAPlugin;
import me.rellynn.foxmc.ffa.game.FFAMode;
import me.rellynn.foxmc.ffa.game.FFAStats;
import me.rellynn.foxmc.ffa.scheduler.AuraEffect;
import me.rellynn.foxmc.ffa.scheduler.AutoRespawn;
import me.rellynn.foxmc.ffa.scheduler.EffectEntityTrack;
import me.rellynn.foxmc.ffa.scheduler.ProjectileTrack;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerJoinGameEvent;
import me.rellynn.foxmc.gameapiv2.games.events.PlayerLeaveGameEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class PlayerListener implements Listener {
    private final FFAPlugin plugin = FFAPlugin.get();
    private final FFAGame game = FFAPlugin.getGame();

    /*
    Effects
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent evt) {
        new ProjectileTrack(evt.getHook());
        if (FoxAPI.getPlayer(evt.getPlayer()).hasShopItem("ffa.effects", "magical_rod"))
            new EffectEntityTrack(evt.getHook(), Effect.FIREWORKS_SPARK, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            new ProjectileTrack(evt.getProjectile());
            if (FoxAPI.getPlayer(player).hasShopItem("ffa.effects", "cupid"))
                new EffectEntityTrack(evt.getProjectile(), Effect.HEART, 2);
        }
    }

    /*
    Soup mode
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        if (evt.hasItem() && evt.getItem().getType() == Material.MUSHROOM_SOUP && !player.isDead() && !game.getSpawn().contains(player.getLocation())) {
            double health = player.getHealth() + 7;
            player.setHealth(health > player.getMaxHealth() ? player.getMaxHealth() : health);
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
        }
    }

    /*
    Methods
     */
    private void giveItems(Player player) {
        player.getInventory().setArmorContents(game.getArmor());
        JsonElement slots = FoxAPI.getPlayer(player).getCustomData("ffa." + game.getMode().getId() + ".slots");
        if (slots == null) {
            player.getInventory().setContents(game.getItems());
        } else {
            Arrays.stream(game.getItems()).forEach(item -> {
                if (item != null) {
                    JsonElement slotElem = slots.getAsJsonObject().get(item.getType().name() + ":" + item.getDurability());
                    int slot = slotElem == null ? player.getInventory().firstEmpty() : slotElem.getAsInt();
                    if (slot != -1)
                        player.getInventory().setItem(slot, item);
                }
            });
        }
    }

    private void updateScoreboard(Player player) {
        Scoreboard board = game.getScoreboard(player);
        if (board == null) {
            board = game.createScoreboard(player, "§cFFA " + game.getMode().getName());
            board.setLine(0, "");
        }
        FFAStats stats = game.getStats().get(player.getUniqueId());
        board.setLine(1, "§8» §7Kills: §a" + game.getGivenStatistic(player.getUniqueId(), "kills"));
        board.setLine(2, "§8» §7Deaths: §c" + game.getGivenStatistic(player.getUniqueId(), "deaths"));
        board.setLine(3, "");
        board.setLine(4, "§8» §7Killstreak: §c" + stats.getKillStreak());
        board.setLine(5, "");
        board.setLine(6, "§eplay.foxmc.net");
    }

    /*
    Game
     */
    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent evt) {
        Player player = evt.getPlayer();
        player.teleport(game.getArena().getPoint("spawn"));
        game.getStats().put(player.getUniqueId(), new FFAStats());
        updateScoreboard(player);
        giveItems(player);
    }

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent evt) {
        Player player = evt.getPlayer();
        PlayerUtils.resetPlayer(player); // Reset inventory (& other things)
        game.getStats().remove(player.getUniqueId());
        game.saveStatistics(evt.getPlayer().getUniqueId());
    }

    /*
    Player
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        evt.setRespawnLocation(game.getArena().getPoint("spawn"));
        new BukkitRunnable() {

            @Override
            public void run() {
                giveItems(evt.getPlayer());
            }
        }.runTaskLater(plugin, 5);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        evt.getDrops().clear();
        evt.setDroppedExp(0);
        evt.setNewTotalExp(0);
        evt.setDeathMessage(null);
        Player player = evt.getEntity();
        game.increaseStatistic(player, "deaths");
        game.getStats().get(player.getUniqueId()).resetKillStreak();
        updateScoreboard(player);
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        new AutoRespawn(player).runTaskLater(plugin, 10);
        Player killer = player.getKiller();
        if (killer != null) {
            FFAStats killerStats = game.getStats().get(player.getKiller().getUniqueId());
            game.increaseStatistic(killer, "kills");
            killerStats.incrementKillStreak();
            updateScoreboard(killer);
            // Messages
            String killerHealth = String.format("%.1f", killer.getHealth() / 2d);
            killer.sendMessage("§7You killed " + player.getDisplayName() + " §7with §c" + killerHealth + " ❤ §7remaining.");
            player.sendMessage("§cYour opponent " + killer.getDisplayName() + " §chad " + killerHealth + " ❤ left.");
            PlayerData killerData = FoxAPI.getPlayer(killer);
            // Coins
            if (!plugin.getKillsManager().hasKilled(killer, player)) {
                game.giveCoins(killer, 2.0F, "kill");
                if (Math.random() > 0.5D) {
                    game.giveTails(killer, 0.5F, "kill bonus");
                }
                plugin.getKillsManager().notifyKill(killer, player);
            }
            // KillStreaks
            int killStreak = killerStats.getKillStreak();
            if (killStreak >= 3) {
                // Aura Effect
                String auraEffect = killerData.getCurrentItem("ffa.aura");
                if (auraEffect != null) {
                    if (killer.hasMetadata("AURA_EFFECT")) {
                        ((AuraEffect) killer.getMetadata("AURA_EFFECT").get(0).value()).reset();
                    } else {
                        killer.setMetadata("AURA_EFFECT", new FixedMetadataValue(plugin, new AuraEffect(killer, Effect.getByName(auraEffect))));
                    }
                }
                game.broadcast(killer.getDisplayName() + " §6is on a Killstreak! §7(§c" + killStreak + " kills in a row§7)");
                // KillStreak Rewards
                if (killStreak == 3) {
                    killer.getInventory().addItem(new ItemStack(Material.TNT));
                } else if (killStreak == 4) {
                    killer.getInventory().addItem(new ItemStack(Material.WEB));
                } else if (killStreak == 5) {
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0));
                } else if (killStreak >= 6) {
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                }
            }
            // Killing Sound
            String killingSound = killerData.getCurrentItem("ffa.sounds");
            Sound sound = Sound.ORB_PICKUP;
            if (killingSound != null) {
                sound = Sound.valueOf(killingSound);
                player.playSound(player.getEyeLocation(), sound, 1, 1);
            }
            killer.playSound(killer.getEyeLocation(), sound, 1, 1);
            if (!killer.isDead()) {
                FFAMode mode = game.getMode();
                if (mode == FFAMode.CLASSIC || mode == FFAMode.OP) {
                    // Regen hearts
                    double health = killer.getHealth() + (2 + killerData.getShopLevel("ffa.upgrades", "health")) * 2;
                    killer.setHealth(health > killer.getMaxHealth() ? killer.getMaxHealth() : health);
                }
                // Give a flint and steel
                int flintSlot = killer.getInventory().first(Material.FLINT_AND_STEEL);
                if (flintSlot == -1) {
                    killer.getInventory().addItem(game.getItems()[3]);
                } else {
                    ItemStack flintAndSteel = killer.getInventory().getItem(flintSlot);
                    flintAndSteel.setAmount(Math.min(64, flintAndSteel.getAmount() + 1));
                }
                // Custom items
                if (mode != FFAMode.OP) {
                    // Give new arrows
                    killer.getInventory().addItem(new ItemStack(Material.ARROW, 2));
                }
                switch (mode) {
                    case UHC:
                        killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
                        break;
                    case SOUP:
                        // Refill soups
                        ItemStack[] contents = killer.getInventory().getContents();
                        for (int i = 0, count = contents.length; i < count; i++) {
                            ItemStack item = contents[i];
                            if (item == null || item.getType() == Material.AIR || item.getType() == Material.BOWL)
                                killer.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                        }
                        break;
                }
                killer.updateInventory();
            }
        }
    }
}
