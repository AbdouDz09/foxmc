package me.rellynn.foxmc.hub.features.cosmetics.effects.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.effects.Effect;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public abstract class AroundHeadEffect extends Effect {
    protected float angle = 45.0F;
    protected double radius = 0.5D;
    protected double offset = 0.0D;

    public AroundHeadEffect(String shopItemId, long period, Rank rank) {
        super(shopItemId, period, rank);
    }

    public AroundHeadEffect(String shopItemId, long period) {
        this(shopItemId, period, Rank.DEFAULT);
    }

    @Override
    protected List<Location> buildLocations() {
        List<Location> locations = new ArrayList<>();
        for (float angle = 0.0F; angle < 360.0F; angle += this.angle) {
            double radians = Math.toRadians(angle);
            locations.add(new Location(null, Math.cos(radians) * radius, 2.0D + offset, Math.sin(radians) * radius));
        }
        return locations;
    }
}
