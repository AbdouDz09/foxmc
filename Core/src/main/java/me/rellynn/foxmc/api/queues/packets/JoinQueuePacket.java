package me.rellynn.foxmc.api.queues.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by gwennaelguich on 21/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinQueuePacket implements FPacket {
    private UUID player;
    private String gameType;
    private String map;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(player.getMostSignificantBits());
        output.writeLong(player.getLeastSignificantBits());
        output.writeUTF(gameType);
        output.writeUTF(map);
    }

    @Override
    public void read(DataInput input) throws IOException {
        player = new UUID(input.readLong(), input.readLong());
        gameType = input.readUTF();
        map = input.readUTF();
    }
}
