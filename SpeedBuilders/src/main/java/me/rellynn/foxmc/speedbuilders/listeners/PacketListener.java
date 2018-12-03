package me.rellynn.foxmc.speedbuilders.listeners;

import io.netty.channel.Channel;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.bukkitapi.utils.TinyProtocol;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by gwennaelguich on 05/07/2017.
 * FoxMC Network.
 */
public class PacketListener extends TinyProtocol {
    private SBGame game = SBPlugin.getGame();

    public PacketListener() {
        super(SBPlugin.get());
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        if (packet instanceof PacketPlayOutNamedEntitySpawn) {
            int id = Reflection.getField(packet.getClass(), int.class, 0).get(packet);
            Optional<Player> specOpt = game.getSpectators().stream().filter(player -> player.getEntityId() == id).findFirst();
            if (specOpt.isPresent()) {
                // Replace spawn packet with morph one
                return game.getMorphPacket(specOpt.get());
            }
        }
        return packet;
    }
}
