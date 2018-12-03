package me.rellynn.foxmc.anticheat.tasks;

import me.rellynn.foxmc.anticheat.ACPlugin;
import me.rellynn.foxmc.anticheat.checks.movement.*;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 22/07/2017.
 * FoxMC Network.
 */
public class HandleMoveAsync extends BukkitRunnable {
    private static final ImpossibleMoveCheck IMPOSSIBLE_MOVE_CHECK = new ImpossibleMoveCheck();
    private static final FlightCheck FLIGHT_CHECK = new FlightCheck();
    private static final SpeedCheck SPEED_CHECK = new SpeedCheck();
    private static final StepCheck STEP_CHECK = new StepCheck();
    private static final WaterWalkCheck WATER_WALK = new WaterWalkCheck();
    private static final FastLadderCheck FAST_LADDER_CHECK = new FastLadderCheck();
    private static final VClipCheck VCLIP_CHECK = new VClipCheck();
    private static final NoSlowdownCheck NO_SLOW_DOWN_CHECK = new NoSlowdownCheck();
    private static final AirJumpCheck AIR_JUMP_CHECK = new AirJumpCheck();

    private Player player;
    private ACPlayer acPlayer;
    private MovingData data;

    public HandleMoveAsync(Player player, ACPlayer acPlayer, MovingData data) {
        this.player = player;
        this.acPlayer = acPlayer;
        this.data = data;
        runTaskAsynchronously(ACPlugin.get());
    }

    @Override
    public void run() {
        if (!data.justTeleported) {
            IMPOSSIBLE_MOVE_CHECK.check(player, acPlayer);
            FLIGHT_CHECK.check(player, acPlayer, data);
            SPEED_CHECK.check(player, acPlayer, data);
            STEP_CHECK.check(player, acPlayer, data);
            WATER_WALK.check(player, acPlayer, data);
            FAST_LADDER_CHECK.check(player, acPlayer, data);
            VCLIP_CHECK.check(player, acPlayer, data);
            NO_SLOW_DOWN_CHECK.check(player, acPlayer, data);
            AIR_JUMP_CHECK.check(player, acPlayer, data);
        }
    }
}
