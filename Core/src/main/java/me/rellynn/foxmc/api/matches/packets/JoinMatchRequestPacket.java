package me.rellynn.foxmc.api.matches.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
public class JoinMatchRequestPacket implements FPacket {
    private int id;
    private String serverName;
    private UUID player;

    public JoinMatchRequestPacket(MatchData match, UUID player) {
        this.id = match.getId();
        this.serverName = match.getServer().getName();
        this.player = player;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeUTF(serverName);
        output.writeLong(player.getMostSignificantBits());
        output.writeLong(player.getLeastSignificantBits());
    }

    @Override
    public void read(DataInput input) throws IOException {
        id = input.readInt();
        serverName = input.readUTF();
        player = new UUID(input.readLong(), input.readLong());
    }
}
