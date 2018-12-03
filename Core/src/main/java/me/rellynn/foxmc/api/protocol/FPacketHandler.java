package me.rellynn.foxmc.api.protocol;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public interface FPacketHandler {

    public void handle(FPacket packet);
}
