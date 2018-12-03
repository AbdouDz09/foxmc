package me.rellynn.foxmc.anticheat.data;

import com.google.common.collect.EvictingQueue;
import me.rellynn.foxmc.anticheat.checks.combat.ConsistencyCheck;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.Queue;

/**
 * Created by gwennaelguich on 29/07/2017.
 * FoxMC Network.
 */
public class FightData {
    public long lastSwingTime;
    public long lastAttackTime;
    public WeakReference<Entity> lastRodEntity;
    public Queue<Long> previousAttacks = EvictingQueue.create(ConsistencyCheck.DATA_VALIDATION);
    /*
    APS
     */
    public long countAttacksStart;
    public int attacksCount;
}
