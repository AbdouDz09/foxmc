package me.rellynn.foxmc.bungeeapi.commands;

import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gwennaelguich on 07/07/2017.
 * FoxMC Network.
 */
public class TSCommand extends Command {

    public TSCommand() {
        super("ts", null, "teamspeak");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        }
        FoxAPI.get().runAsync(() -> {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            try {
                URL url = new URL("https://foxmc.net/api/privilege_key.php?uuid=" + player.getUniqueId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                String result = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
                if (result.contains("error code")) {
                    throw new IllegalStateException(result);
                }
                player.sendMessage(new ComponentBuilder("Click here")
                        .underlined(true)
                        .color(ChatColor.GOLD)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://foxmc.net/pages/teamspeak/?token=" + result + "&username=" + player.getName()))
                        .append(" to join our TeamSpeak!")
                        .underlined(false)
                        .color(ChatColor.YELLOW)
                        .create());
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(TextComponent.fromLegacyText("§cAn error occurred. Please try again later."));
            }
        });
    }
}
