package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class AntiPotionCheck extends Check {

    public AntiPotionCheck() {
        super("AntiPotion", 15, 2.0F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        Collection<PotionEffect> effects = player.getActivePotionEffects();
        if (!player.isDead() && acPlayer.knownEffects != null && !acPlayer.knownEffects.isEmpty()) {
            int avgMovePerTick = (int) Math.round(movingData.packetsCount / (double) 20),
                    maxMovePacketsPerTick = movingData.getMaxMovePacketsPerTick();
            if (avgMovePerTick > (maxMovePacketsPerTick * 2) && acPlayer.knownEffects.stream().anyMatch(effect -> !player.hasPotionEffect(effect.getType()) && effect.getDuration() > maxMovePacketsPerTick)) {
                handleActions(true, acPlayer, avgMovePerTick - maxMovePacketsPerTick);
                return;
            }
            // Decrease the threshold
            handleActions(false, acPlayer);
        }
        acPlayer.knownEffects = effects;
    }
}
