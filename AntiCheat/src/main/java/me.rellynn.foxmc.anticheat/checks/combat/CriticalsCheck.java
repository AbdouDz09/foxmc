package me.rellynn.foxmc.anticheat.checks.combat;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class CriticalsCheck extends Check {

    public CriticalsCheck() {
        super("Criticals", 8, 1.5F);
    }

    public boolean passCheck(Player player, ACPlayer acPlayer, MovingData movingData, double originalDamage) {
        double exceptedDamage = ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        return handleActions(originalDamage == exceptedDamage * 1.5D && !movingData.isFalling(), acPlayer);
    }
}
