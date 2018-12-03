package me.rellynn.foxmc.api.pubsub;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public interface PubSubReceiver {

    public void receive(String channel, String message);
}
