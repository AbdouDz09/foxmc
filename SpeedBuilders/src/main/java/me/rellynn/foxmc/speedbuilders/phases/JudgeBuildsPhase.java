package me.rellynn.foxmc.speedbuilders.phases;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.TimerPhase;
import me.rellynn.foxmc.gameapiv2.utils.EntityUtils;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public class JudgeBuildsPhase extends TimerPhase<SBGame> {
    private Map<Platform, Float> scores = new HashMap<>();
    @Getter private Platform<Player> looserPlatform;

    public JudgeBuildsPhase() {
        super(GameState.RUNNING, 180L, 1L);
    }

    @Override
    protected void onStart() {
        // Copy build to middle
        game.getCurrentBuild().paste(game.getMiddlePlatform());
        // Spectator mode & titles
        game.getAllPlayers().forEach(player -> {
            player.setGameMode(GameMode.SPECTATOR);
            PacketUtils.sendTitle("§cBUILD TIME OVER!", player);
            PacketUtils.sendSubTitle("§7Master Ghast will judge your builds", player);
            PacketUtils.sendActionBar("", player);
        });
        // Get scores
        float lowestScore = 1.0F;
        for (Platform<Player> platform : game.getPlatforms()) {
            float score = game.getScore(platform);
            scores.put(platform, score);
            if (score <= lowestScore) {
                lowestScore = score;
                looserPlatform = platform;
            }
        }
    }

    @Override
    protected void onTimerEvent(TimerEvent evt) {
        Ghast ghast = game.getMiddlePlatform().getEntity();
        if (evt == TimerEvent.TICK) {
            if (ticksLeft > 35L) {
                Location location = ghast.getLocation();
                location.setYaw(location.getYaw() + 2.5F);
                EntityUtils.setPosition(ghast, location);
            } else if (ticksLeft == 35L) {
                game.getPlatforms().forEach(platform -> {
                    Player player = platform.getEntity();
                    PacketUtils.sendTitle("", player);
                    PacketUtils.sendSubTitle("§7You scored: §e" + (int) (scores.get(platform) * 100) + "%", player);
                });
                // Face entity to looser platform
                EntityUtils.lookAt(ghast, looserPlatform.getPaste().clone().add(3.0D, 0.0D, 3.0D));
            }
        } else if (evt == TimerEvent.END) {
            Player worstPlayer = looserPlatform.getEntity();
            String message = worstPlayer.getDisplayName() + " §cis eliminated!";
            game.getAllPlayers().forEach(player -> {
                if (player != worstPlayer) {
                    PacketUtils.sendTitle("", player);
                    PacketUtils.sendSubTitle(message, player);
                } else {
                    PacketUtils.sendTitle("§cYOU LOST!", player);
                    PacketUtils.sendSubTitle("§7You have been eliminated!", player);
                    worstPlayer.sendMessage("§cYou have been eliminated!");
                    Location location = worstPlayer.getLocation();
                    location.setY(looserPlatform.getMaxLocation().getY() + 1.0D);
                    worstPlayer.teleport(location);
                    game.setSpectator(worstPlayer);
                }
                Location soundLocation = player.getEyeLocation();
                player.getWorld().playSound(soundLocation, Sound.GHAST_SCREAM, 1.0F, 1.0F);
                player.getWorld().playSound(soundLocation, Sound.GHAST_FIREBALL, 1.0F, 1.0F);
            });
            // Throw fireballs!!!
            ghast.launchProjectile(Fireball.class, ghast.getLocation().getDirection().normalize().multiply(1.5D));
        }
    }

    @Override
    protected void onEnd() {
        scores.clear();
        looserPlatform = null;
        game.getMiddlePlatform().clearBuild();
    }
}
