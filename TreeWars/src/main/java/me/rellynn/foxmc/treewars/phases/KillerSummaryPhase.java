package me.rellynn.foxmc.treewars.phases;

import me.rellynn.foxmc.bukkitapi.utils.TextUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.treewars.game.TWGame;

import java.util.List;
import java.util.UUID;

/**
 * Created by gwennaelguich on 05/06/2017.
 * FoxMC Network.
 */
public class KillerSummaryPhase extends Phase<TWGame> {

    public KillerSummaryPhase() {
        super(GameState.ENDING);
        setDelay(75L);
    }

    @Override
    protected void onTick() {
        List<UUID> top = game.getTopStatistics("kills");
        String[] messages = new String[]{
                "",
                TextUtils.center("§a§lTreeWars"),
                "",
                TextUtils.center(game.getWinners()),
                "",
                TextUtils.center("§a§l1st Killer §7- " + (top.size() < 1 ? "Nobody" : game.getDisplayName(top.get(0)) + "§7 - " + game.getGivenStatistic(top.get(0), "kills"))),
                TextUtils.center("§e§l2nd Killer §7- " + (top.size() < 2 ? "Nobody" : game.getDisplayName(top.get(1)) + "§7 - " + game.getGivenStatistic(top.get(1), "kills"))),
                TextUtils.center("§c§l3rd Killer §7- " + (top.size() < 3 ? "Nobody" : game.getDisplayName(top.get(2)) + "§7 - " + game.getGivenStatistic(top.get(2), "kills"))),
                "",
                ""
        };
        game.getAllPlayers().forEach(player -> player.sendMessage(messages));
    }
}
