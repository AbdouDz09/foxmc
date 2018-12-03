package me.rellynn.foxmc.api.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public interface FPacket {

    public void write(DataOutput output) throws IOException;

    public void read(DataInput input) throws IOException;
}
