package me.rellynn.foxmc.api.moderation.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.protocol.FPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModerationMessagePacket implements FPacket {
    private BaseComponent[] message;

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(ComponentSerializer.toString(message));
    }

    @Override
    public void read(DataInput input) throws IOException {
        message = ComponentSerializer.parse(input.readUTF());
    }
}
