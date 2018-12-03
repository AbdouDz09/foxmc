package me.rellynn.foxmc.api.settings;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.types.FriendSetting;
import me.rellynn.foxmc.api.settings.types.ToggleSetting;

/**
 * Created by gwennaelguich on 17/08/2017.
 * FoxMC Network.
 */
public class Settings {
    public static final FriendSetting privateMessages = new FriendSetting("private_messages");
    public static final ToggleSetting friendRequests = new ToggleSetting("friend_requests");
    public static final FriendSetting partyInvites = new FriendSetting("party_invites");
    public static final ToggleSetting hubVisibility = new ToggleSetting("hub_visibility");
    public static final ToggleSetting hubProtection = new ToggleSetting("hub_protection");
    public static final ToggleSetting hubFly = new ToggleSetting("hub_fly", Rank.VIP);
}
