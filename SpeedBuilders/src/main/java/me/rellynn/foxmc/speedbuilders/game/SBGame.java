package me.rellynn.foxmc.speedbuilders.game;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.Scoreboard;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.phases.EndingPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.ShutdownPhase;
import me.rellynn.foxmc.gameapiv2.games.phases.WaitingPhase;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.phases.*;
import me.rellynn.foxmc.speedbuilders.utils.Build;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftGhast;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
@Getter
public class SBGame extends Game {
    private Platform<Ghast> middlePlatform;
    private Set<Platform<Player>> platforms = new HashSet<>();
    private Set<Platform> perfects = new HashSet<>();
    private Build currentBuild;
    private int rounds;
    private String winner;

    public SBGame(Arena arena) {
        super("sbuilders", "SpeedBuilders", arena);
        loadArena();
    }

    /*
    Game loading
     */
    @Override
    protected void onLoad() {
        addPhase(new WaitingPhase(10));
        addPhase(new StartPhase());
        addPhase(new ShowBuildPhase());
        addPhase(new BuildingPhase());
        addPhase(new JudgeBuildsPhase());
        addPhase(new SummaryPhase());
        addPhase(new EndingPhase(6));
        addPhase(new ShutdownPhase());
    }

    private <T extends Entity> Platform<T> loadPlatform(String name, T entity) {
        String key = name + ".";
        Location pasteLocation = getArena().getPoint(key + "paste");
        Location spawnLocation = getArena().getPoint(key + "spawn");
        return new Platform(entity, getArena().getPoint(key + "pos1"), getArena().getPoint(key + "pos2"), pasteLocation, spawnLocation);
    }

    private void loadArena() {
        Location location = getArena().getPoint("middle.spawn");
        Ghast ghast = location.getWorld().spawn(location, Ghast.class);
        ((CraftGhast) ghast).getHandle().b(true); // Silent
        ((CraftGhast) ghast).getHandle().k(true); // NoAI
        middlePlatform = loadPlatform("middle", ghast);
    }

    /*
    Game
     */
    public void gotNewRound() {
        rounds++;
        perfects.clear();
        currentBuild = SBPlugin.get().pickBuild();
        updateScoreboards(this::updateRoundInfos);
        getPlatforms().forEach(platform -> {
            Player player = platform.getEntity();
            PlayerUtils.resetPlayer(player);
            player.teleport(platform.getSpawn());
        });
    }

    public void giveRewards() {
        getPlayers().forEach(player -> {
            giveCoins(player, 2.5F, "round win");
            giveTails(player, 1.0F, "round win bonus");
            increaseStatistic(player, "rounds_wins");
        });
    }

    public boolean isReallyPerfect() {
        return perfects.size() >= platforms.size();
    }

    public boolean alreadyGotPerfect(Platform platform) {
        return perfects.contains(platform);
    }

    public void gotPerfectBuild(Platform platform) {
        perfects.add(platform);
        if (isReallyPerfect()) {
            getAllPlayers().forEach(player -> {
                PacketUtils.sendTitle("", player);
                PacketUtils.sendSubTitle("§bGood job, Perfect!", player);
            });
            giveRewards();
            new BukkitRunnable() {

                @Override
                public void run() {
                    setPhase(ShowBuildPhase.class);
                }
            }.runTaskLater(SBPlugin.get(), 30L);
        }
    }

    public boolean checkWin() {
        if (platforms.size() == 1) {
            Platform<Player> platform = platforms.iterator().next();
            Player winner = platform.getEntity();
            this.winner = winner.getDisplayName();
            String winnerNameMsg = winner.getDisplayName() + " §6won the game!";
            String congratsMsg = "§a§k|§b§k|§c§k|§d§k|§e§k|§b§l Congratulations §a§k|§b§k|§c§k|§d§k|§e§k|";
            String message = winnerNameMsg + " " + congratsMsg;
            getAllPlayers().forEach(player -> {
                player.sendMessage(message);
                PacketUtils.sendTitle(winnerNameMsg, player);
                PacketUtils.sendSubTitle(congratsMsg, player);
            });
            giveCoins(winner, 1.0F * rounds, "win");
            giveTails(winner, 0.5F * rounds, "win bonus");
            increaseStatistic(winner, "games_wins");
            end();
            return true;
        }
        return false;
    }

    /*
    Players
     */
    private void updateRoundInfos(Scoreboard board) {
        board.setLine(1, "§8» §7Build: " + (currentBuild == null ? "§cNone" : "§b" + currentBuild.getName()));
        board.setLine(2, "§8» §7Round: " + (rounds == 0 ? "§c0" : "§6" + rounds));
    }

    private void updatePlayers(Scoreboard board) {
        int line = 5;
        for (Platform<Player> platform : platforms) {
            board.setLine(line++, "   " + platform.getEntity().getDisplayName());
        }
        // Remove old lines
        for (int index = line; index < 15; index++) {
            board.removeLine(index);
        }
    }

    public void createScoreboard(Player player) {
        if (!hasScoreboard(player)) {
            Scoreboard board = createScoreboard(player, "§eSpeedBuilders");
            board.setLine(0, "");
            updateRoundInfos(board);
            board.setLine(3, "");
            board.setLine(4, "§8» §7Players:");
            updatePlayers(board);
        }
    }

    public Packet getMorphPacket(Player player) {
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(((CraftPlayer) player).getHandle());
        Reflection.getField(spawnPacket.getClass(), "b", int.class).set(spawnPacket, EntityType.SQUID.getTypeId());
        Reflection.getField(spawnPacket.getClass(), "l", DataWatcher.class).set(spawnPacket, new DataWatcher(null));
        return spawnPacket;
    }

    /*
    Platforms
     */
    public float getScore(Platform platform) {
        return currentBuild == null || alreadyGotPerfect(platform) ? 1.0F : currentBuild.getScore(platform);
    }

    public Platform<Player> getPlatform(Player player) {
        return platforms.stream().filter(platform -> platform.isEntity(player)).findFirst().orElse(null);
    }

    public Platform<Player> getPlatform(Location location) {
        return platforms.stream().filter(platform -> platform.contains(location)).findFirst().orElse(null);
    }

    public void givePlatforms() {
        for (Player player : getPlayers()) {
            Platform<Player> platform = loadPlatform("platform" + (platforms.size() + 1), player);
            player.teleport(platform.getSpawn());
            platforms.add(platform);
        }
    }

    public void removePlatform(Platform<Player> platform) {
        platforms.remove(platform);
        updateScoreboards(this::updatePlayers);
        // Falling blocks effect
        platform.getBlocks().forEach(block -> {
            if (block.getType() != Material.AIR && Math.random() >= 0.7D) {
                FallingBlock entity = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                entity.setDropItem(false);
                entity.setHurtEntities(false);
                entity.setVelocity(Vector.getRandom());
            }
        });
        platform.clear();
    }
}
