package me.rellynn.foxmc.api.parties.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

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
public class PartyMessagePacket implements FPacket {
    private UUID party;
    private BaseComponent[] message;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(party.getMostSignificantBits());
        output.writeLong(party.getLeastSignificantBits());
        output.writeUTF(ComponentSerializer.toString(message));
    }

    @Override
    public void read(DataInput input) throws IOException {
        party = new UUID(input.readLong(), input.readLong());
        message = ComponentSerializer.parse(input.readUTF());
    }
}
