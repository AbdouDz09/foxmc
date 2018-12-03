package me.rellynn.foxmc.gameapiv2.games.phases;

import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import org.bukkit.Bukkit;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public abstract class TimerPhase<T extends Game> extends Phase<T> {
    protected long totalTicks;
    protected long ticksLeft;
    private long ticking;

    public TimerPhase(GameState state, long ticks, long interval) {
        super(state);
        this.totalTicks = ticks;
        this.ticking = interval;
        setTicking(interval);
    }

    public TimerPhase(GameState state, long ticks) {
        this(state, ticks, 20L);
    }

    @Override
    public void enable() {
        ticksLeft = totalTicks;
        super.enable();
    }

    protected abstract void onTimerEvent(TimerEvent evt);

    @Override
    protected void onTick() {
        if (ticksLeft <= 0) {
            onTimerEvent(TimerEvent.END);
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
            return;
        }
        onTimerEvent(TimerEvent.TICK);
        ticksLeft -= ticking;
    }

    public enum TimerEvent {
        TICK,
        END
    }
}
