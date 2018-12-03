package me.rellynn.foxmc.speedbuilders.phases;

import me.rellynn.foxmc.bukkitapi.utils.TextUtils;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.speedbuilders.game.SBGame;

/**
 * Created by gwennaelguich on 07/07/2017.
 * FoxMC Network.
 */
public class SummaryPhase extends Phase<SBGame> {

    public SummaryPhase() {
        super(GameState.ENDING);
        setDelay(75L);
    }

    @Override
    protected void onTick() {
        game.getAllPlayers().forEach(player -> {
            player.sendMessage("");
            player.sendMessage(TextUtils.center("§lGame Summary"));
            player.sendMessage("");
            player.sendMessage(TextUtils.center("§d§lWinner: §r§n" + game.getWinner()));
            player.sendMessage("");
            player.sendMessage(TextUtils.center("§eYou earned a total of"));
            player.sendMessage(TextUtils.center("§6+§l" + game.getGivenCoins(player) + " §6FoxCoins"));
            player.sendMessage(TextUtils.center("§a+§l" + game.getGivenTails(player) + " §aTails"));
            player.sendMessage("");
            player.sendMessage("");
        });
    }
}
