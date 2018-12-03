package me.rellynn.foxmc.api.matches.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.protocol.FPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Getter
@NoArgsConstructor
public class MatchCreatePacket implements FPacket {
    private int id;
    private String serverName;
    private String game;
    private String map;
    private String state;
    private int maxPlayers;
    private Set<String> filters;

    public MatchCreatePacket(MatchData match) {
        this.id = match.getId();
        this.serverName = match.getServer().getName();
        this.game = match.getGame();
        this.map = match.getMap();
        this.state = match.getState();
        this.maxPlayers = match.getMaxPlayers();
        this.filters = match.getFilters();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeUTF(serverName);
        output.writeUTF(game);
        output.writeUTF(map);
        output.writeUTF(state);
        output.writeInt(maxPlayers);
        output.writeInt(filters.size());
        for (String filter : filters) {
            output.writeUTF(filter);
        }
    }

    @Override
    public void read(DataInput input) throws IOException {
        id = input.readInt();
        serverName = input.readUTF();
        game = input.readUTF();
        map = input.readUTF();
        state = input.readUTF();
        maxPlayers = input.readInt();
        filters = new HashSet<>();
        int filtersCount = input.readInt();
        for (int i = 0; i < filtersCount; i++) {
            filters.add(input.readUTF());
        }
    }
}
