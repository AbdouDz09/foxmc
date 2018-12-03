package me.rellynn.foxmc.api.moderation.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerUnmutePacket implements FPacket {
    private UUID target;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(target.getMostSignificantBits());
        output.writeLong(target.getLeastSignificantBits());
    }

    @Override
    public void read(DataInput input) throws IOException {
        target = new UUID(input.readLong(), input.readLong());
    }
}
