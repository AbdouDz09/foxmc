package me.rellynn.foxmc.api.servers.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.servers.ServerData;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
public class ServerKeepAlivePacket implements FPacket {
    private String serverName;

    public ServerKeepAlivePacket(ServerData server) {
        this.serverName = server.getName();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(serverName);
    }

    @Override
    public void read(DataInput input) throws IOException {
        serverName = input.readUTF();
    }
}
