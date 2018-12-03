package me.rellynn.foxmc.api.players.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by gwennaelguich on 26/06/2017.
 * FoxMC Network.
 */
@Table("mod_ipbans")
public class BanIPEntry extends Model {

    public static BanIPEntry getLastBan(String ip) {
        List<BanIPEntry> bans = BanIPEntry.find("ip = ?", ip).orderBy("date DESC").limit(1);
        return bans.isEmpty() ? null : bans.get(0);
    }

    public BanIPEntry() {}

    public BanIPEntry(String ip, String reason, String bannedBy, long until) {
        set("ip", ip);
        set("reason", reason);
        set("banned_by", bannedBy);
        set("date", System.currentTimeMillis());
        set("until", until);
        saveIt();
    }

    public String getIPAddress() {
        return getString("ip");
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
