package me.rellynn.foxmc.api.parties.packets;

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
public class PartyPlayerKickPacket implements FPacket {
    private UUID party;
    private String leader;
    private UUID player;
    private String playerName;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(party.getMostSignificantBits());
        output.writeLong(party.getLeastSignificantBits());
        output.writeUTF(leader);
        output.writeLong(player.getMostSignificantBits());
        output.writeLong(player.getLeastSignificantBits());
        output.writeUTF(playerName);
    }

    @Override
    public void read(DataInput input) throws IOException {
        party = new UUID(input.readLong(), input.readLong());
        leader = input.readUTF();
        player = new UUID(input.readLong(), input.readLong());
        playerName = input.readUTF();
    }
}
