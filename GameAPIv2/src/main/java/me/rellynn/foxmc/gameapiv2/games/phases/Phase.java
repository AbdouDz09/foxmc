package me.rellynn.foxmc.gameapiv2.games.phases;

import lombok.Getter;
import lombok.Setter;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public abstract class Phase<T extends Game> implements Runnable {
    @Setter protected T game;
    protected int taskId = -1;
    @Getter private GameState state;
    private long interval = -1;
    private long delay = -1;

    public Phase(GameState state) {
        this.state = state;
    }

    @Override
    public void run() {
        try {
            onTick();
        } catch (Exception ex) {
            GameAPIv2.get().getLogger().severe("Error while ticking " + getClass().getSimpleName() + " phase:");
            ex.printStackTrace();
        }
    }

    protected void onStart() {}

    protected void onTick() {}

    protected void onEnd() {}

    protected void setTicking(long interval) {
        this.interval = interval;
    }

    protected void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isState(GameState state) {
        return this.state == state;
    }

    public boolean isJoinable() {
        return isState(GameState.WAITING) && game.getPlayers().size() < game.getArena().getMaxPlayers();
    }

    public boolean canJoin(Player player) {
        if (isJoinable()) {
            return true;
        } else if (game.isUsingTeams() && game.getArena().getPlayerTeam(player) != null) {
            return true;
        }
        return false;
    }

    public void enable() {
        onStart();
        if (interval != -1) {
            taskId = Bukkit.getScheduler().runTaskTimer(GameAPIv2.get(), this, 0L, interval).getTaskId();
        } else if (delay != -1) {
            taskId = new BukkitRunnable() {

                @Override
                public void run() {
                    taskId = -1;
                    Phase.this.run();
                    game.nextPhase();
                }
            }.runTaskLater(GameAPIv2.get(), delay).getTaskId();
        }
    }

    public void disable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        onEnd();
    }
}
