package me.rellynn.foxmc.api.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.anticheat.packets.ACViolationPacket;
import me.rellynn.foxmc.api.friends.packets.FriendConnectPacket;
import me.rellynn.foxmc.api.friends.packets.FriendRequestPacket;
import me.rellynn.foxmc.api.friends.packets.FriendResponsePacket;
import me.rellynn.foxmc.api.friends.packets.FriendTeleportPacket;
import me.rellynn.foxmc.api.matches.packets.*;
import me.rellynn.foxmc.api.moderation.packets.*;
import me.rellynn.foxmc.api.network.packets.BroadcastMessagePacket;
import me.rellynn.foxmc.api.parties.packets.PartyMessagePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerInvitePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerKickPacket;
import me.rellynn.foxmc.api.players.packets.*;
import me.rellynn.foxmc.api.pubsub.PubSubReceiver;
import me.rellynn.foxmc.api.servers.packets.*;
import org.apache.commons.codec.binary.Base64;

import java.io.DataInput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class FProtocolManager implements PubSubReceiver {
    private static FProtocolManager instance;

    static {
        FProtocolManager protocol = get();
        // Server packets
        protocol.registerPacket(0, ServerCreatePacket.class);
        protocol.registerPacket(1, ServerClosePacket.class);
        protocol.registerPacket(2, ServerKeepAlivePacket.class);
        protocol.registerPacket(3, ServerChangeStatePacket.class);
        protocol.registerPacket(4, ServerUpdatePlayersPackets.class);
        protocol.registerPacket(5, PlayerVotePacket.class);
        // Network packets
        protocol.registerPacket(100, BroadcastMessagePacket.class);
        // Match packets
        protocol.registerPacket(200, MatchCreatePacket.class);
        protocol.registerPacket(201, MatchEndPacket.class);
        protocol.registerPacket(202, MatchChangeStatePacket.class);
        protocol.registerPacket(203, MatchUpdatePlayersPacket.class);
        protocol.registerPacket(204, JoinMatchRequestPacket.class);
        // Player packets
        protocol.registerPacket(300, PlayerConnectPacket.class);
        protocol.registerPacket(301, PlayerMessagePacket.class);
        protocol.registerPacket(302, PlayerSetRankPacket.class);
        protocol.registerPacket(303, PlayerNickChangePacket.class);
        protocol.registerPacket(304, PlayerSetAmountPacket.class);
        protocol.registerPacket(305, PlayerSetDataPacket.class);
        protocol.registerPacket(306, PlayerVotePacket.class);
        // Moderation
        protocol.registerPacket(400, ModerationMessagePacket.class);
        protocol.registerPacket(401, PlayerTeleportPacket.class);
        protocol.registerPacket(402, PlayerKickPacket.class);
        protocol.registerPacket(403, PlayerMutePacket.class);
        protocol.registerPacket(404, PlayerUnmutePacket.class);
        // Friends
        protocol.registerPacket(500, FriendRequestPacket.class);
        protocol.registerPacket(501, FriendResponsePacket.class);
        protocol.registerPacket(502, FriendConnectPacket.class);
        protocol.registerPacket(503, FriendTeleportPacket.class);
        // Parties
        protocol.registerPacket(600, PartyPlayerInvitePacket.class);
        protocol.registerPacket(601, PartyMessagePacket.class);
        protocol.registerPacket(602, PartyPlayerKickPacket.class);
        // AntiCheat
        protocol.registerPacket(700, ACViolationPacket.class);
    }

    public static FProtocolManager get() {
        if (instance == null) {
            instance = new FProtocolManager();
        }
        return instance;
    }

    private Set<FPacketHandler> handlers = new HashSet<>();
    private Map<Integer, Class<? extends FPacket>> incomingPackets = new HashMap<>();
    private Map<Class<? extends FPacket>, Integer> outgoingPackets = new HashMap<>();

    private FProtocolManager() {
        registerChannels("broadcast");
    }

    public void registerChannels(String... channels) {
        for (String channel : channels) {
            CoreAPI.get().getPubSub().subscribe("packets." + channel, this);
        }
    }

    public void registerHandler(FPacketHandler handler) {
        handlers.add(handler);
    }

    private void registerPacket(int id, Class<? extends FPacket> packet) {
        incomingPackets.put(id, packet);
        outgoingPackets.put(packet, id);
    }

    private FPacket readPacket(byte[] bytes) {
        try {
            DataInput input = ByteStreams.newDataInput(bytes);
            int packetId = input.readInt();
            Class<? extends FPacket> clazz = incomingPackets.get(packetId);
            if (clazz == null) {
                throw new RuntimeException("Packet not registered!");
            }
            FPacket packet = clazz.newInstance();
            packet.read(input);
            return packet;
        } catch (Exception e) {
            CoreAPI.get().getLogger().severe("Unable to read packet:");
            e.printStackTrace();
        }
        return null;
    }

    private ByteArrayDataOutput writePacket(FPacket packet) {
        try {
            Integer packetId = outgoingPackets.get(packet.getClass());
            if (packetId == null) {
                throw new RuntimeException("Packet not registered!");
            }
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeInt(packetId);
            packet.write(output);
            return output;
        } catch (Exception e) {
            CoreAPI.get().getLogger().severe("Unable to write packet:");
            e.printStackTrace();
        }
        return null;
    }

    public void sendPacket(FPacket packet, String... channels) {
        ByteArrayDataOutput output = writePacket(packet);
        if (output != null) {
            for (String channel : channels) {
                CoreAPI.get().getPubSub().send("packets." + channel, Base64.encodeBase64String(output.toByteArray()));
            }
        }
    }

    public void sendToServer(String server, FPacket packet) {
        sendPacket(packet, "servers." + server);
    }

    public void sendToProxy(String proxy, FPacket packet) {
        sendPacket(packet, "proxies." + proxy);
    }

    public void broadcastServers(FPacket packet) {
        sendPacket(packet, "servers");
    }

    public void broadcastProxies(FPacket packet) {
        sendPacket(packet, "proxies");
    }

    public void broadcast(FPacket packet) {
        sendPacket(packet, "broadcast");
    }

    @Override
    public void receive(String channel, String message) {
        FPacket packet = readPacket(Base64.decodeBase64(message));
        if (packet != null) {
            handlers.forEach(handler -> handler.handle(packet));
        }
    }
}
