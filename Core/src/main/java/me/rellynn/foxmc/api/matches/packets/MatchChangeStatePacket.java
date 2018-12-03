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
public class MatchChangeStatePacket implements FPacket {
    private int id;
    private String serverName;
    private String newState;
    private boolean joinable;

    public MatchChangeStatePacket(MatchData match) {
        this.id = match.getId();
        this.serverName = match.getServer().getName();
        this.newState = match.getState();
        this.joinable = match.isJoinable();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeUTF(serverName);
        output.writeUTF(newState);
        output.writeBoolean(joinable);
    }

    @Override
    public void read(DataInput input) throws IOException {
        id = input.readInt();
        serverName = input.readUTF();
        newState = input.readUTF();
        joinable = input.readBoolean();
    }
}
