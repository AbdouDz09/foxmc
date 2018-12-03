package me.rellynn.foxmc.hub.npcentities;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.hub.api.HubAPI;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 25/05/2017.
 * FoxMC Network.
 */
public class NPCCommand extends PlayerCommand {
    private final HubAPI hubAPI = HubAPI.get();

    NPCCommand() {
        super(Rank.ADMIN);
    }

    private void spawnNPC(Player player, String entityType) {
        EntityType type = EntityType.fromName(entityType);
        if (type == null || !EntityLiving.class.isAssignableFrom(type.getEntityClass())) {
            player.sendMessage("§cUnknown entity type: §e" + entityType);
            return;
        }
        NPC npc = hubAPI.getNpcManager().spawnNPC(type, player.getLocation());
        player.sendMessage("§aNPC spawned! §r(id=" + npc.getId() + ")");
    }

    private void setName(Player player, int npcId, String name) {
        NPC npc = hubAPI.getNpcManager().getNPC(npcId);
        if (npc == null) {
            player.sendMessage("§cUnknown NPC!");
            return;
        }
        npc.setName(name);
        player.sendMessage("§aNPC name changed!");
    }

    private void setEquipment(Player player, int npcId, int slot) {
        NPC npc = hubAPI.getNpcManager().getNPC(npcId);
        if (npc == null) {
            player.sendMessage("§cUnknown NPC!");
        } else if (!npc.setEquipment(slot, player.getItemInHand())) {
            player.sendMessage("§cUnknown slot: §e" + slot);
        } else {
            player.sendMessage("§aEntity equipment updated!");
        }
    }

    private void setAction(Player player, int npcId, String action, String value) {
        NPC npc = hubAPI.getNpcManager().getNPC(npcId);
        if (npc == null) {
            player.sendMessage("§cUnknown NPC!");
            return;
        }
        try {
            NPC.ClickAction clickAction = NPC.ClickAction.valueOf(action);
            npc.setInteractAction(clickAction, value);
            player.sendMessage("§aNPC action updated! §r(action=" + clickAction.name() + ", value=" + value + ")");
        } catch (IllegalArgumentException ex) {
            player.sendMessage("§cUnknown action: §e" + action);
        }
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0];
            switch (subCommand) {
                case "spawn":
                    if (args.length == 1) {
                        player.sendMessage("§cUsage: §e/npc spawn <entitytype>");
                        break;
                    }
                    spawnNPC(player, args[1]);
                    break;
                case "kill":
                    if (args.length == 1 || !args[1].matches("[0-9]+")) {
                        player.sendMessage("§cUsage: §e/npc kill <npcid>");
                    } else if (!hubAPI.getNpcManager().removeNPC(Integer.parseInt(args[1]))) {
                        player.sendMessage("§cUnknown NPC: §e" + args[1]);
                    } else {
                        player.sendMessage("§aNPC " + args[1] + " killed!");
                    }
                    break;
                case "setname":
                    if (args.length < 3 || !args[1].matches("[0-9]+")) {
                        player.sendMessage("§cUsage: §e/npc setname <npcid> <name>");
                        break;
                    }
                    setName(player, Integer.parseInt(args[1]), StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " "));
                    break;
                case "setitem":
                    if (args.length < 3 || !args[1].matches("[0-9]+") || !args[2].matches("[0-9]+")) {
                        player.sendMessage("§cUsage: §e/npc setitem <npcid> <slot>");
                        break;
                    }
                    setEquipment(player, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    break;
                case "setaction":
                    if (args.length < 3 || !args[1].matches("[0-9]+")) {
                        player.sendMessage("§cUsage: §e/npc setaction <npcid> <message> [value]");
                        break;
                    }
                    setAction(player, Integer.parseInt(args[1]), args[2].toUpperCase(), StringUtils.join(Arrays.copyOfRange(args, 3, args.length), " "));
                    break;
            }
        }
    }
}
