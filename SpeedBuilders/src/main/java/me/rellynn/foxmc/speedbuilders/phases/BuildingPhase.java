package me.rellynn.foxmc.speedbuilders.phases;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.TimerPhase;
import me.rellynn.foxmc.gameapiv2.utils.BroadcastType;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.utils.Build;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
@Getter
public class BuildingPhase extends TimerPhase<SBGame> {
    private long startTime;

    public BuildingPhase() {
        super(GameState.RUNNING, 800L);
    }

    @Override
    protected void onStart() {
        game.broadcast("§7You have §e40 seconds §7to reproduce the build shown!");
        Build build = game.getCurrentBuild();
        game.getPlatforms().forEach(platform -> {
            platform.setEditAllowed(true);
            Player player = platform.getEntity();
            player.getInventory().addItem(build.getItems());
            player.setAllowFlight(true);
            player.updateInventory();
        });
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onTimerEvent(TimerEvent evt) {
        if (!game.isReallyPerfect()) {
            if (evt == TimerEvent.END) {
                game.nextPhase();
                return;
            }
            // Timer is running
            StringBuilder text = new StringBuilder("§oBUILDING TIME: ");
            float progress = (float) ticksLeft / totalTicks;
            int steps = (int) (progress * 40);
            for (int i = 0; i < steps; i++) {
                text.append("§a§l￨");
            }
            int missing = 40 - steps;
            for (int i = 0; i < missing; i++) {
                text.append("§c§l￨");
            }
            game.broadcast(BroadcastType.ACTIONBAR, text.toString());
        }
    }

    @Override
    protected void onEnd() {
        startTime = 0;
        game.getPlatforms().forEach(platform -> platform.setEditAllowed(false));
        game.getPlayers().forEach(player -> {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().clear();
            player.updateInventory();
        });
    }
}
