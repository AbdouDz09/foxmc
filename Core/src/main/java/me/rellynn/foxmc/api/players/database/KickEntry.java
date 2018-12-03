package me.rellynn.foxmc.api.players.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
@Table("mod_kicks")
public class KickEntry extends Model {

    public KickEntry() {}

    public KickEntry(UUID uuid, String reason, String kickedBy) {
        set("uuid", uuid + "");
        set("reason", reason);
        set("kicked_by", kickedBy);
        set("date", System.currentTimeMillis());
        saveIt();
    }

    public UUID getPlayer() {
        return UUID.fromString(getString("uuid"));
    }

    public String getReason() {
        return getString("reason");
    }

    public String getKickedBy() {
        return getString("kicked_by");
    }

    public Date getDate() {
        return new Date(getLong("date"));
    }
}
