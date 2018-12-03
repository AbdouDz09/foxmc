package me.rellynn.foxmc.bungeeapi.moderation.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rellynn.foxmc.api.moderation.packets.PlayerMutePacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.MuteEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.scheduler.MuteExpireTask;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
public class MuteCommand extends ModerationCommand {
    private static Map<UUID, MuteData> mutedPlayers = new HashMap<>();

    /**
     * Register the muted player in the memory.
     * It is used as a cache to avoid network requests.
     *
     * @param player   The player
     * @param reason   The mute reason
     * @param duration The mute duration (in milliseconds)
     */
    public static void addMuteData(ProxiedPlayer player, String reason, long duration) {
        MuteData data = new MuteData(System.currentTimeMillis() + duration, reason, ProxyServer.getInstance().getScheduler().schedule(BridgePlugin.get(), new MuteExpireTask(player), duration, TimeUnit.MILLISECONDS));
        mutedPlayers.put(player.getUniqueId(), data);
    }

    /**
     * Retrieve the player mute data from the memory.
     *
     * @param player The player
     * @return The mute data or null
     */
    public static MuteData getMuteData(ProxiedPlayer player) {
        return mutedPlayers.get(player.getUniqueId());
    }

    /**
     * Remove the player mute data from the memory.
     *
     * @param uuid The player unique id
     */
    public static void removeMuteData(UUID uuid) {
        MuteData data = mutedPlayers.remove(uuid);
        if (data != null && data.task.getId() != -1) {
            data.task.cancel();
        }
    }

    private FoxAPI foxAPI = FoxAPI.get();

    public MuteCommand() {
        super("mute", Rank.HELPER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/mute <player> <duration> [reason]"));
            return;
        }
        FoxAPI.get().getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
            if (target == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find the player §e" + args[0] + " §c!"));
                return;
            } else if (sender instanceof ProxiedPlayer && !canSanction((ProxiedPlayer) sender, uuid)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cYou may not mute this player!"));
                return;
            }
            long duration;
            try {
                duration = TimeUtils.getDuration(args[1]);
            } catch (Exception ex) {
                sender.sendMessage(TextComponent.fromLegacyText("§cThe mute duration is invalid!"));
                return;
            }
            if (sender instanceof ProxiedPlayer) {
                Rank rank = FoxAPI.getPlayer((ProxiedPlayer) sender).getRank();
                if (rank == Rank.MODERATOR && duration > 14400000) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cYou can't go over §e4 hours§c!"));
                    return;
                } else if (rank == Rank.HELPER && duration > 7200000) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cYou can't go over §e2 hours§c!"));
                    return;
                }
            }
            String reason = args.length < 3 ? "No reason specified" : StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
            sendModMessage(TextComponent.fromLegacyText(PREFIX + "§e" + target.getName() + " §6has been muted §e" + TimeUtils.getDuration(duration) + " §6by §e" + sender.getName() + " §6for §r" + reason));
            new MuteEntry(uuid, reason, !(sender instanceof ProxiedPlayer) ? "CONSOLE" : ((ProxiedPlayer) sender).getUniqueId() + "", System.currentTimeMillis() + duration);
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName != null) {
                FProtocolManager.get().sendToProxy(proxyName, new PlayerMutePacket(uuid, duration, reason));
            }
        });
    }

    @Getter
    @AllArgsConstructor
    public static class MuteData {
        private long until;
        private String reason;
        private ScheduledTask task;
    }
}
