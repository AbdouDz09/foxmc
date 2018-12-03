package me.rellynn.foxmc.api.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessagePacket implements FPacket {
    private String message;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(message);
    }

    @Override
    public void read(DataInput input) throws IOException {
        message = input.readUTF();
    }
}
