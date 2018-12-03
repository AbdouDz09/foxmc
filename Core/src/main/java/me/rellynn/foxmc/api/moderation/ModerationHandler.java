package me.rellynn.foxmc.api.moderation;

import me.rellynn.foxmc.api.moderation.packets.*;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public abstract class ModerationHandler implements FPacketHandler {

    protected ModerationHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof ModerationMessagePacket) {
            handleMessage((ModerationMessagePacket) packet);
        } else if (packet instanceof PlayerTeleportPacket) {
            handleTeleport((PlayerTeleportPacket) packet);
        } else if (packet instanceof PlayerKickPacket) {
            handleKick((PlayerKickPacket) packet);
        } else if (packet instanceof PlayerMutePacket) {
            handleMute((PlayerMutePacket) packet);
        } else if (packet instanceof PlayerUnmutePacket) {
            handleUnmute((PlayerUnmutePacket) packet);
        }
    }

    protected void handleMessage(ModerationMessagePacket packet) {}

    protected void handleTeleport(PlayerTeleportPacket packet) {}

    protected void handleKick(PlayerKickPacket packet) {}

    protected void handleMute(PlayerMutePacket packet) {}

    protected void handleUnmute(PlayerUnmutePacket packet) {}
}
