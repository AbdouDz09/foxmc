package me.rellynn.foxmc.games.scheduler;

import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.game.api.BroadcastType;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.DuelGameInfo;
import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 01/05/2017.
 * FoxMC Network.
 */
public class BeginRoundTask extends BukkitRunnable {
    private DuelGameInfo duelInfo;
    private int seconds;

    public BeginRoundTask(DuelGameInfo duelInfo) {
        this.duelInfo = duelInfo;
        this.seconds = 2;
        runTaskTimer(DuelsPlugin.get(), 20, 20);
    }

    @Override
    public void run() {
        Game game = duelInfo.getGame();
        if (seconds > 0) {
            game.broadcast(BroadcastType.TITLE, "§c" + seconds + " SECOND" + (seconds > 1 ? "S" : ""));
            game.broadcast("§7The fight starts in §6" + seconds + " second" + (seconds > 1 ? "s" : "") + ".");
            seconds--;
        } else {
            cancel();
            for (Player player : game.getPlayers()) {
                PlayerUtils.resetPlayer(player);
                player.getInventory().setArmorContents(duelInfo.getArmorContents());
                player.getInventory().setContents(duelInfo.getContents());
                player.updateInventory();
                PacketUtils.sendTitle("§6GO! FIGHT!", player);
            }
            duelInfo.startRoundTimer();
        }
    }
}
