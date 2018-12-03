package me.rellynn.foxmc.gameapiv2.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.commands.PlayerCommand;
import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class ArenaCommand extends PlayerCommand {
    private final GameAPIv2 gameAPI = GameAPIv2.get();

    public ArenaCommand() {
        super(Rank.OWNER);
    }

    @Override
    public void onCommand(Player player, String label, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: §e/arena <id> <subcommand> [args]");
            return;
        }
        Arena arena = GameAPIv2.get().getArenasManager().getArena(args[0]);
        if (arena == null && !args[1].equalsIgnoreCase("create")) {
            player.sendMessage("§cUnknown arena: §e" + args[0]);
            return;
        } else if (arena == null) {
            gameAPI.getArenasManager().registerArena(args[0], "default", "Default", 2, Bukkit.getMaxPlayers());
            player.sendMessage("§aArena " + args[0] + " created!");
            return;
        }
        String subCommand = args[1].toLowerCase();
        switch (subCommand) {
            case "setmode":
                if (args.length == 2) {
                    player.sendMessage("§cUsage: §e/arena <id> setmode <mode>");
                    break;
                }
                String mode = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
                arena.setMode(mode);
                player.sendMessage("§aArena mode set to §r\"§b" + mode + "§r\"");
                break;
            case "setmin":
                if (args.length == 2 || !args[2].matches("[0-9]+")) {
                    player.sendMessage("§cUsage: §e/arena <id> setmin <players>");
                    break;
                }
                arena.setMinPlayers(Integer.parseInt(args[2]));
                player.sendMessage("§aArena minimum players set to §r\"§b" + args[2] + "§r\"");
                break;
            case "setmax":
                if (args.length == 2 || !args[2].matches("[0-9]+")) {
                    player.sendMessage("§cUsage: §e/arena <id> setmax <players>");
                    break;
                }
                arena.setMaxPlayers(Integer.parseInt(args[2]));
                player.sendMessage("§aArena maximum players set to §r\"§b" + args[2] + "§r\"");
                break;
            case "setpoint":
                if (args.length == 2) {
                    player.sendMessage("§cUsage: §e/arena <id> setpoint <name>");
                    break;
                }
                arena.setPoint(args[2], player.getLocation());
                player.sendMessage("§aPoint §r\"" + args[2] + "§r\" §adefined!");
                break;
            case "delpoint":
                if (args.length == 2) {
                    player.sendMessage("§cUsage: §e/arena <id> delpoint <name>");
                    break;
                }
                arena.removePoint(args[2]);
                player.sendMessage("§cPoint §r\"" + args[2] + "§r\" §cremoved!");
                break;
            case "tp":
                if (args.length == 2) {
                    player.sendMessage("§cUsage: §e/arena <id> tp <name>");
                    break;
                }
                Location location = arena.getPoint(args[2]);
                if (location == null) {
                    player.sendMessage("§cUnknown point: §e" + args[2]);
                    break;
                }
                player.teleport(location);
                break;
            case "team":
                if (args.length < 5) {
                    player.sendMessage("§cUsage: §e/arena <id> team <id> <prefix> <name>");
                    break;
                }
                String name = StringUtils.join(Arrays.copyOfRange(args, 4, args.length), " ");
                arena.registerTeam(args[2], args[3], name);
                player.sendMessage("§aTeam " + ChatColor.translateAlternateColorCodes('&', name) + " §aregistered!");
                break;
            case "teampoint":
                if (args.length < 4) {
                    player.sendMessage("§cUsage: §e/arena <id> teampoint <teamid> <spawnid>");
                    break;
                }
                Team team = arena.getTeam(args[2]);
                if (team == null) {
                    player.sendMessage("§cUnknown team: §e" + args[2]);
                    player.sendMessage("§7Use §a/arena <id> team <id> <prefix> <name> §7to register a new team.");
                    return;
                }
                team.setPoint(args[3], player.getLocation());
                player.sendMessage("§aPoint §r\"" + args[3] + "\" for \"" + team.getDisplayName() + "§r\" §adefined!");
                break;
            default:
                player.sendMessage("§cUnknown subcommand: " + subCommand);
                break;
        }
    }
}
