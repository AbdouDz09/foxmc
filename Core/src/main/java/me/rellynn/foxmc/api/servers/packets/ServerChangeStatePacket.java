package me.rellynn.foxmc.api.servers.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServerState;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
public class ServerChangeStatePacket implements FPacket {
    private String serverName;
    private ServerState newState;

    public ServerChangeStatePacket(ServerData data) {
        this.serverName = data.getName();
        this.newState = data.getState();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(serverName);
        output.writeUTF(newState.name());
    }

    @Override
    public void read(DataInput input) throws IOException {
        serverName = input.readUTF();
        newState = ServerState.valueOf(input.readUTF());
    }
}
