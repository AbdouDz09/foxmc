package me.rellynn.foxmc.api.players.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
@Table("mod_mutes")
public class MuteEntry extends Model {

    public MuteEntry() {}

    public MuteEntry(UUID uuid, String reason, String mutedBy, long until) {
        set("uuid", uuid + "");
        set("reason", reason);
        set("muted_by", mutedBy);
        set("date", System.currentTimeMillis());
        set("until", until);
        saveIt();
    }

    public UUID getPlayer() {
        return UUID.fromString(getString("uuid"));
    }

    public String getReason() {
        return getString("reason");
    }

    public String getMutedBy() {
        return getString("muted_by");
    }

    public String getUnmutedBy() {
        return getString("unmuted_by");
    }

    public Date getUnmuteDate() {
        Long unmuteAt = getLong("unmuted_date");
        return unmuteAt == null ? null : new Date(unmuteAt);
    }

    public Date getDate() {
        return new Date(getLong("date"));
    }

    public Date getUntilDate() {
        return new Date(getLong("until"));
    }

    public long getDuration() {
        return getLong("until") - System.currentTimeMillis();
    }

    public void unmute(String unmutedBy) {
        set("unmuted_by", unmutedBy);
        set("unmuted_date", System.currentTimeMillis());
        saveIt();
    }

    public boolean isActive() {
        return getDuration() > 0 && getUnmutedBy() == null;
    }
}
