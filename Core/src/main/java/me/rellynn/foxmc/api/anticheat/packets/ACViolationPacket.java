package me.rellynn.foxmc.api.anticheat.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ACViolationPacket implements FPacket {
    private UUID player;
    private String checkName;
    private int ping;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(player.getMostSignificantBits());
        output.writeLong(player.getLeastSignificantBits());
        output.writeUTF(checkName);
        output.writeInt(ping);
    }

    @Override
    public void read(DataInput input) throws IOException {
        player = new UUID(input.readLong(), input.readLong());
        checkName = input.readUTF();
        ping = input.readInt();
    }
}
