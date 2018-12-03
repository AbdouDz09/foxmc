package me.rellynn.foxmc.bukkitapi.utils;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public class FireworkUtils {
    private static final Random RANDOM = new Random();
    private static final FireworkEffect.Type[] TYPES = {
            FireworkEffect.Type.BALL,
            FireworkEffect.Type.BALL_LARGE,
            FireworkEffect.Type.BURST,
            FireworkEffect.Type.STAR
    };

    /**
     * Spawn a random firework to the specified location.
     *
     * @param location The location
     * @return The spawned firework entity
     */
    public static Firework spawnRandomFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .flicker(RANDOM.nextBoolean())
                .withColor(ColorUtils.getRandom())
                .withFade(ColorUtils.getRandom())
                .with(TYPES[RANDOM.nextInt(TYPES.length)])
                .trail(RANDOM.nextBoolean())
                .build()
        );
        meta.setPower(RANDOM.nextInt(1) + 1);
        firework.setFireworkMeta(meta);
        return firework;
    }
}
