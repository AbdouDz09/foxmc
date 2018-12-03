package me.rellynn.foxmc.treewars.game;

import lombok.Getter;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.bukkitapi.utils.Scoreboard;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.phases.EndingPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.ShutdownPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.WaitingPhase;
import me.rellynn.foxmc.gameapiv2.utils.Cuboid;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.generators.Generator;
import me.rellynn.foxmc.treewars.game.generators.types.BlueOrchidGenerator;
import me.rellynn.foxmc.treewars.game.generators.types.CocoaGenerator;
import me.rellynn.foxmc.treewars.game.generators.types.DeadBushGenerator;
import me.rellynn.foxmc.treewars.game.generators.types.SeedsGenerator;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.MiningSpeedUpgrade;
import me.rellynn.foxmc.treewars.phases.KillerSummaryPhase;
import me.rellynn.foxmc.treewars.phases.RunningPhase;
import me.rellynn.foxmc.treewars.phases.WinnerSummaryPhase;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
@Getter
public class TWGame extends Game {

    private static String getGame(Arena arena) {
        return arena.getMode().equals("solo") ? "TreeWars §cSolo" : "TreeWars §eTeams";
    }

    private Cuboid lobby;
    private Map<Team, Tree> trees = new HashMap<>();
    private Map<Team, Inventory> teamInventories = new HashMap<>();
    private Set<Generator> generators = new HashSet<>();
    private String winners = "§7Who is the winner???";
    private Set<Team> deadTeams = new HashSet<>();

    public TWGame(Arena arena) {
        super("twars", getGame(arena), arena);
    }

    public boolean isSoloMode() {
        return getArena().getMode().equals("solo");
    }

    @Override
    protected void onLoad() {
        addPhase(new WaitingPhase(isSoloMode() ? 10 : 30));
        addPhase(new RunningPhase());
        addPhase(new KillerSummaryPhase());
        addPhase(new WinnerSummaryPhase());
        addPhase(new EndingPhase(3));
        addPhase(new ShutdownPhase());
    }

    @Override
    public boolean canChooseTeam(Player player) {
        return !isSoloMode() || FoxAPI.getPlayer(player).isAtLeast(Rank.VIP_PLUS);
    }

    private Map<String, Location> filterPoints(Map<String, Location> points, String prefix) {
        Map<String, Location> locations = new HashMap<>();
        points.forEach((s, location) -> {
            if (s.startsWith(prefix)) {
                locations.put(s, location);
            }
        });
        return locations;
    }

    private void createEntity(EntityType type, Location location, String name, String menu) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        entity.setMetadata("OPEN_MENU", new FixedMetadataValue(TWPlugin.get(), menu));
        ArmorStand hologram = location.getWorld().spawn(location, ArmorStand.class);
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.setCustomName(name);
        hologram.setCustomNameVisible(true);
        EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) entity).getHandle();
        nmsEntity.b(true); // Silent
        nmsEntity.k(true); // NoAI
        nmsEntity.f(nmsEntity.yaw); // Fix yaw
        nmsEntity.g(nmsEntity.yaw); // Fix yaw
    }

    private void addGenerator(Generator generator, String text, double yOffset) {
        ArmorStand hologram = generator.getLocation().getWorld().spawn(generator.getLocation().clone().add(0.5D, yOffset, 0.5D), ArmorStand.class);
        hologram.setSmall(true);
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.setMetadata("GENERATOR", new FixedMetadataValue(TWPlugin.get(), generator));
        hologram.setCustomName(text);
        hologram.setCustomNameVisible(true);
        generators.add(generator);
    }

    private void addWorldGen(Generator generator) {
        addGenerator(generator, generator.getDrop().getPrefix() + "§l" + generator.getDrop().getName(), 3.35D);
    }

    public void setupArena() {
        // Lobby Cuboid
        lobby = new Cuboid(getArena().getPoint("lobby1"), getArena().getPoint("lobby2"));
        // Map Generators
        filterPoints(getArena().getPoints(), "blueorchid_gen").forEach((id, location) -> addWorldGen(new BlueOrchidGenerator(id, location)));
        filterPoints(getArena().getPoints(), "deadbush_gen").forEach((id, location) -> addWorldGen(new DeadBushGenerator(id, location)));
        // Teams
        for (Team team : getArena().getTeams()) {
            trees.put(team, new Tree(team));
            // Shops
            createEntity(EntityType.VILLAGER, team.getPoint("shop"), "§a§lITEMS SHOP", "shop");
            createEntity(EntityType.WITCH, team.getPoint("upgrades"), "§6§lUPGRADES", "upgrades");
            // Generators
            filterPoints(team.getPoints(), "seeds_gen").forEach((id, location) -> addGenerator(new SeedsGenerator(id + "_" + team.getId(), location), "§7Right-click to upgrade", 0.0D));
            filterPoints(team.getPoints(), "cocoa_gen").forEach((id, location) -> addGenerator(new CocoaGenerator(id + "_" + team.getId(), location), "§eRight-click to upgrade", 0.0D));
            // Team Inventory
            Inventory inventory = Bukkit.createInventory(null, 27, "Team Chest");
            teamInventories.put(team, inventory);
        }
    }

    public Tree getTree(Team team) {
        return trees.get(team);
    }

    public void updateBoards() {
        getAllPlayers().forEach(player -> {
            Scoreboard board = getScoreboard(player);
            if (board == null) {
                board = createScoreboard(player, "§e§lTreeWars" + (isSoloMode() ? " §e§oSOLO" : ""));
                board.setLine(0, "");
                board.setLine(10, "");
                board.setLine(13, "");
                board.setLine(14, "§eplay.foxmc.net");
            }
            int line = 1;
            for (Team team : getArena().getTeams()) {
                if (deadTeams.contains(team)) {
                    board.removeLine(line);
                } else {
                    board.setLine(line, ChatColor.translateAlternateColorCodes('&', team.getPrefix()) + "§r" + ChatColor.stripColor(team.getDisplayName()) + ": " + (trees.get(team).isDead() ? "§c✘" : "§a✔"));
                }
                line++;
            }
            board.setLine(11, "Kills: §a" + getGivenStatistic(player, "kills"));
            board.setLine(12, "Final Kills: §a" + getGivenStatistic(player, "final_kills"));
        });
    }

    public void respawn(Player player) {
        Team team = getArena().getPlayerTeam(player);
        PlayerUtils.resetPlayer(player);
        player.removeMetadata("RESPAWNING", TWPlugin.get());
        // Mining Speed Upgrade
        if (TWPlugin.getGame().getTree(team).hasUpgrade(Upgrade.MINING_SPEED)) {
            player.addPotionEffect(MiningSpeedUpgrade.EFFECT);
        }
        // Spawn Protection
        player.setMetadata("SPAWN_PROTECTION", new FixedMetadataValue(TWPlugin.get(), System.currentTimeMillis()));
        // Respawn
        player.getInventory().setArmorContents((ItemStack[]) player.getMetadata("PERMANENT_ARMOR").get(0).value());
        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
        player.teleport(team.getPoint("spawn"));
        player.updateInventory();
    }

    /*
    Ticking functions
     */
    public void tickGenerators() {
        generators.forEach(Generator::tick);
    }

    public void tickTeams() {
        boolean needsUpdate = false;
        Set<Team> leftTeams = new HashSet<>();
        for (Team team : getArena().getTeams()) {
            if (!team.getOnlinePlayers(this).isEmpty()) {
                leftTeams.add(team);
            } else if (deadTeams.add(team)) {
                String displayName = !isSoloMode() || team.getPlayers().isEmpty() ? team.getDisplayName() + " Team" : getDisplayName(team.getPlayers().iterator().next());
                team.clear();
                trees.get(team).destroy();
                broadcast(displayName + " §chas been eliminated!");
                needsUpdate = true;
            }
        }
        if (needsUpdate) {
            updateBoards();
            if (leftTeams.size() == 1) {
                Team winner = leftTeams.iterator().next();
                winners = winner.getDisplayName() + " §7- " + StringUtils.join(winner.getOnlinePlayers(this).stream().map(this::getDisplayName).collect(Collectors.toSet()), ", ");
                String displayName = !isSoloMode() ? winner.getDisplayName() + " Team" : getDisplayName(winner.getPlayers().iterator().next());
                broadcast(displayName + " §6won the game! §a§k|§b§k|§c§k|§d§k|§e§k|§b§l Congratulations §a§k|§b§k|§c§k|§d§k|§e§k|");
                winner.getOnlinePlayers(this).forEach(player -> {
                    increaseStatistic(player, "wins");
                    giveCoins(player, 8.0F, "victory");
                    giveTails(player, 3.0F, "victory bonus");
                    PacketUtils.sendTitle("§aVICTORY!", player);
                });
                end();
            }
        }
    }
}
