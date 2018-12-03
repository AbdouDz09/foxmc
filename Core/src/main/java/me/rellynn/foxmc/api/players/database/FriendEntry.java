package me.rellynn.foxmc.api.players.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by gwennaelguich on 03/05/2017.
 * FoxMC Network.
 */
@Table("friends")
public class FriendEntry extends Model {

    public FriendEntry() {}

    public FriendEntry(UUID player, UUID friend) {
        set("friend_one", player + "");
        set("friend_two", friend + "");
        set("time", System.currentTimeMillis());
        saveIt();
    }

    public Date getDate() {
        return new Date(getLong("time"));
    }
}
