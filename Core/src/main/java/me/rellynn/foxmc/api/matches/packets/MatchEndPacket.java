package me.rellynn.foxmc.api.matches.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
public class MatchEndPacket implements FPacket {
    private int id;
    private String serverName;

    public MatchEndPacket(MatchData match) {
        this.id = match.getId();
        this.serverName = match.getServer().getName();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeUTF(serverName);
    }

    @Override
    public void read(DataInput input) throws IOException {
        id = input.readInt();
        serverName = input.readUTF();
    }
}
