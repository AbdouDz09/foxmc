package me.rellynn.foxmc.anticheat.data;

import com.google.common.collect.EvictingQueue;
import me.rellynn.foxmc.anticheat.ACConfig;
import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.api.anticheat.packets.ACViolationPacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import net.minecraft.server.v1_8_R3.PacketPlayInTransaction;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Created by gwennaelguich on 16/07/2017.
 * FoxMC Network.
 */
public class ACPlayer {
    private static final Map<Player, ACPlayer> CACHED_PLAYERS = new WeakHashMap<>();

    public static ACPlayer get(Player player) {
        return CACHED_PLAYERS.computeIfAbsent(player, ACPlayer::new);
    }

    /*
    Player data
     */
    public Player player;
    public MovingData movingData;
    public FightData fightData;
    /*
    Checks
     */
    public long lastInteractTime;
    public long lastRegenTime;
    public long lastBlockPlaced;
    public boolean inventoryOpened;
    public boolean checkSlowdown;
    public long checkSlowdownStart;
    public ItemStack lastItemInHand;
    public Collection<PotionEffect> knownEffects;
    /*
    Private data
     */
    private Queue<Integer> lastPings = EvictingQueue.create(ACConfig.KEEP_LAST_PINGS);
    private short pingTransactionId;
    private long pingTransactionTime;
    private Map<Check, Long> reports = new HashMap<>();
    private Map<Check, Float> violations = new HashMap<>();

    private ACPlayer(Player player) {
        this.player = player;
        this.movingData = new MovingData(player, this);
        this.fightData = new FightData();
    }

    public void updatePing() {
        if (pingTransactionId == 0 && System.currentTimeMillis() - pingTransactionTime >= ACConfig.PING_REFRESH) {
            pingTransactionId = (short) ((Math.random() * Short.MIN_VALUE) - 1);
            pingTransactionTime = System.currentTimeMillis();
            PacketUtils.sendPacket(player, new PacketPlayOutTransaction(0, pingTransactionId, false));
        }
    }

    public void handleTransaction(PacketPlayInTransaction packet) {
        if (packet.a() == 0 && pingTransactionId == packet.b()) {
            lastPings.add((int) (System.currentTimeMillis() - pingTransactionTime));
            pingTransactionId = 0;
        }
    }

    public int getPing() {
        if (pingTransactionId != 0 && (System.currentTimeMillis() - pingTransactionTime) >= ACConfig.HIGH_PING) {
            return ACConfig.HIGHEST_PING;
        }
        return Math.min(lastPings.stream().mapToInt(Integer::intValue).max().orElse(ACConfig.HIGHEST_PING), ACConfig.HIGHEST_PING);
    }

    /*
    Thresholds
     */
    public float getThreshold(Check check) {
        return violations.getOrDefault(check, 0.0F);
    }

    public void increaseThreshold(Check check, float amount) {
        violations.compute(check, (aClass, value) -> Math.max(0, value == null ? amount : (value + amount)));
    }

    public void decreaseThreshold(Check check, float amount) {
        increaseThreshold(check, -amount);
    }

    public void failedCheck(Check check, float threshold) {
        long now = System.currentTimeMillis();
        Long lastReport = reports.get(check);
        int ping = getPing();
        if ((lastReport == null || now - lastReport >= ACConfig.NOTIFY_ANTISPAM) && (ping < ACConfig.HIGH_PING || threshold >= check.getMaxThreshold() * 2)) {
            reports.put(check, now);
            violations.remove(check);
            if (ACConfig.DEBUG_MODE) {
                Bukkit.broadcastMessage("§7[§cANTICHEAT§7] §e" + player.getName() + " §o(" + FoxAPI.get().getServerData().getName() + ") §6failed §r" + check.getName() + " check (ping=" + ping + "ms)");
            } else {
                // Send violation packet
                ACViolationPacket packet = new ACViolationPacket(player.getUniqueId(), check.getName(), ping);
                FProtocolManager.get().broadcastProxies(packet);
            }
        }
    }
}
