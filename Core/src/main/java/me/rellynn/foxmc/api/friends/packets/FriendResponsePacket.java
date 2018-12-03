package me.rellynn.foxmc.api.friends.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponsePacket implements FPacket {
    private UUID player;
    private UUID target;
    private boolean accepted;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(player.getMostSignificantBits());
        output.writeLong(player.getLeastSignificantBits());
        output.writeLong(target.getMostSignificantBits());
        output.writeLong(target.getLeastSignificantBits());
        output.writeBoolean(accepted);
    }

    @Override
    public void read(DataInput input) throws IOException {
        player = new UUID(input.readLong(), input.readLong());
        target = new UUID(input.readLong(), input.readLong());
        accepted = input.readBoolean();
    }
}
