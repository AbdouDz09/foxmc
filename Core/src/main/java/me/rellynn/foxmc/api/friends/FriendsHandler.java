package me.rellynn.foxmc.api.friends;

import me.rellynn.foxmc.api.friends.packets.FriendConnectPacket;
import me.rellynn.foxmc.api.friends.packets.FriendRequestPacket;
import me.rellynn.foxmc.api.friends.packets.FriendResponsePacket;
import me.rellynn.foxmc.api.friends.packets.FriendTeleportPacket;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public abstract class FriendsHandler implements FPacketHandler {

    protected FriendsHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof FriendConnectPacket) {
            onFriendConnect((FriendConnectPacket) packet);
        } else if (packet instanceof FriendRequestPacket) {
            onFriendRequest((FriendRequestPacket) packet);
        } else if (packet instanceof FriendResponsePacket) {
            onFriendResponse((FriendResponsePacket) packet);
        } else if (packet instanceof FriendTeleportPacket) {
            onFriendTeleport((FriendTeleportPacket) packet);
        }
    }

    /*
    Hooks
     */
    public void onFriendConnect(FriendConnectPacket packet) {}

    public void onFriendRequest(FriendRequestPacket packet) {}

    public void onFriendResponse(FriendResponsePacket packet) {}

    public void onFriendTeleport(FriendTeleportPacket packet) {}
}
