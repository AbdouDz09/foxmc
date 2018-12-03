package me.rellynn.foxmc.anticheat.checks.player;

import me.rellynn.foxmc.anticheat.ACConfig;
import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 11/08/2017.
 * FoxMC Network.
 */
public class AntiHungerCheck extends Check {

    public AntiHungerCheck() {
        super("AntiHunger / PotionSaver", 8, 8.0F);
    }

    public void check(Player player, ACPlayer acPlayer, MovingData movingData) {
        if (!player.isDead() && player.getSpectatorTarget() == null) {
            if (movingData.packetsCount == 1 && acPlayer.getPing() < ACConfig.HIGHEST_PING && movingData.previousPackets.stream().allMatch(packet -> packet instanceof PacketPlayInFlying.PacketPlayInPosition)) {
                handleActions(true, acPlayer);
                return;
            }
            // Reset the threshold
            handleActions(false, acPlayer);
        }
    }
}
