package me.rellynn.foxmc.treewars.phases;

import me.rellynn.foxmc.bukkitapi.utils.TextUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.treewars.game.TWGame;

/**
 * Created by gwennaelguich on 12/06/2017.
 * FoxMC Network.
 */
public class WinnerSummaryPhase extends Phase<TWGame> {

    public WinnerSummaryPhase() {
        super(GameState.ENDING);
        setDelay(75L);
    }

    @Override
    protected void onTick() {
        game.getAllPlayers().forEach(player -> {
            player.sendMessage("");
            player.sendMessage(TextUtils.center("§lGame Summary"));
            player.sendMessage("");
            player.sendMessage(TextUtils.center(game.getWinners()));
            player.sendMessage("");
            player.sendMessage(TextUtils.center("§eYou earned a total of"));
            player.sendMessage(TextUtils.center("§e+§l" + game.getGivenCoins(player) + " §eFoxCoins"));
            player.sendMessage(TextUtils.center("§a+§l" + game.getGivenTails(player) + " §aTails"));
            player.sendMessage("");
            player.sendMessage("");
        });
    }
}
