package me.rellynn.foxmc.bukkitapi.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.TextUtils;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 17/04/2017.
 * FoxMC Network.
 */
public class LagCommand extends PlayerCommand {

    public LagCommand() {
        super(Rank.DEFAULT);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        player.sendMessage("");
        player.sendMessage(TextUtils.center("§c§lLAG INFORMATIONS"));
        player.sendMessage("");
        player.sendMessage(TextUtils.center("§7Server: §e" + FoxAPI.get().getServerData().getName()));
        player.sendMessage("");
        player.sendMessage(TextUtils.center("§7Ping: §e" + getPing(player) + " ms"));
        player.sendMessage("");
        player.sendMessage(TextUtils.center("§7Ticks per second: §e" + formatTPS(MinecraftServer.getServer().recentTps[0])));
        player.sendMessage("");
        player.sendMessage("");
    }

    private String getPing(Player player) {
        int ping = ((CraftPlayer) player).getHandle().ping;
        return (ping > 200 ? "§c" : (ping > 120 ? "§6" : ping > 70 ? "§e" : "§a")) + ping;
    }

    private String formatTPS(double tps) {
        return (tps > 18 ? "§a" : (tps > 16 ? "§e" : "§c")) + (tps > 20 ? "*" : "") + Math.round(Math.min(tps, 20) * 100) / 100d;
    }
}
