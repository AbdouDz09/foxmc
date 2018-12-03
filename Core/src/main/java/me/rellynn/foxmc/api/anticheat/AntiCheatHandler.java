package me.rellynn.foxmc.api.anticheat;

import me.rellynn.foxmc.api.anticheat.packets.ACViolationPacket;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public abstract class AntiCheatHandler implements FPacketHandler {

    public AntiCheatHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof ACViolationPacket) {
            handleViolation((ACViolationPacket) packet);
        }
    }

    protected void handleViolation(ACViolationPacket packet) {}
}
