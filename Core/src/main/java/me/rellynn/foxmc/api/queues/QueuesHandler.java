package me.rellynn.foxmc.api.queues;

import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.queues.packets.JoinQueuePacket;
import me.rellynn.foxmc.api.queues.packets.LeaveQueuePacket;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
public abstract class QueuesHandler implements FPacketHandler {

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof JoinQueuePacket) {
            handleJoin((JoinQueuePacket) packet);
        } else if (packet instanceof LeaveQueuePacket) {
            handleLeave((LeaveQueuePacket) packet);
        }
    }

    protected void handleJoin(JoinQueuePacket packet) {}

    protected void handleLeave(LeaveQueuePacket packet) {}
}
