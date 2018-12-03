package me.rellynn.foxmc.bungeeapi.scheduler;

import lombok.AllArgsConstructor;
import me.rellynn.foxmc.bungeeapi.moderation.commands.MuteCommand;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by gwennaelguich on 16/05/2017.
 * FoxMC Network.
 */
@AllArgsConstructor
public class MuteExpireTask implements Runnable {
    private ProxiedPlayer player;

    @Override
    public void run() {
        MuteCommand.removeMuteData(player.getUniqueId());
        player.sendMessage(TextComponent.fromLegacyText("§cYour mute punishment is over!"));
    }
}
