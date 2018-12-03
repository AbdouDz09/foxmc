package me.rellynn.foxmc.bukkitapi.events;

import lombok.Getter;
import me.rellynn.foxmc.api.matches.MatchData;

import java.util.Set;
import java.util.UUID;

/**
 * Created by gwennaelguich on 18/08/2017.
 * FoxMC Network.
 */
@Getter
public class PartyJoinMatchEvent extends PlayerJoinMatchEvent {
    private UUID party;
    private Set<UUID> members;

    public PartyJoinMatchEvent(UUID player, MatchData match, UUID party, Set<UUID> members) {
        super(player, match);
        this.party = party;
        this.members = members;
    }
}
