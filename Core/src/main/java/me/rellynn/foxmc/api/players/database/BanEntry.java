package me.rellynn.foxmc.api.players.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
@Table("mod_bans")
public class BanEntry extends Model {

    public BanEntry() {}

    public BanEntry(UUID uuid, String reason, String bannedBy, long until) {
        set("uuid", uuid + "");
        set("reason", reason);
        set("banned_by", bannedBy);
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

    public String getBannedBy() {
        return getString("banned_by");
    }

    public String getUnbannedBy() {
        return getString("unbanned_by");
    }

    public Date getUnbanDate() {
        Long unbanAt = getLong("unbanned_date");
        return unbanAt == null ? null : new Date(unbanAt);
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

    public void unban(String unbanBy) {
        set("unbanned_by", unbanBy);
        set("unbanned_date", System.currentTimeMillis());
        saveIt();
    }

    public boolean isActive() {
        return getDuration() > 0 && getUnbannedBy() == null;
    }
}
