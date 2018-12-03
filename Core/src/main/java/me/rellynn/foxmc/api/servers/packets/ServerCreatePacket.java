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
public class ServerCreatePacket implements FPacket {
    private String name;
    private String host;
    private int port;
    private int maxPlayers;

    public ServerCreatePacket(ServerData data) {
        this.name = data.getName();
        this.host = data.getHost();
        this.port = data.getPort();
        this.maxPlayers = data.getMaxPlayers();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(name);
        output.writeUTF(host);
        output.writeInt(port);
        output.writeInt(maxPlayers);
    }

    @Override
    public void read(DataInput input) throws IOException {
        name = input.readUTF();
        host = input.readUTF();
        port = input.readInt();
        maxPlayers = input.readInt();
    }
}
