package me.rellynn.foxmc.gameapiv2.menus.items;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.gameapiv2.games.Game;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
public class JoinTeamItem extends VirtualItem {
    private Game game;
    private Team team;
    private ItemStack item;

    public JoinTeamItem(Game game, Team team) {
        super(game.getMatch().getName() + "_team_" + team.getId(), ActionType.CUSTOM, "", 1);
        this.game = game;
        this.team = team;
        this.item = new ItemBuilder(Material.INK_SACK, (short) DyeColor.valueOf(team.getId().toUpperCase()).getDyeData())
                .setTitle(team.getDisplayName() + " Team")
                .build();
    }

    @Override
    protected void onClick(Player player, PerformedAction action) {
        Team playerTeam = game.getArena().getPlayerTeam(player);
        if (team != playerTeam) {
            int teamPlayers = team.getPlayers().size();
            if (teamPlayers != 0 && teamPlayers >= Math.ceil(game.getPlayers().size() / game.getArena().getTeams().size())) {
                player.sendMessage("§cYou can't join this team: too many players.");
                return;
            }
            // Leave old team
            if (playerTeam != null) {
                playerTeam.leave(game, player);
            }
            // Join the new one
            team.join(game, player);
            getUglyMenu(player).updateAllOpened();
            player.sendMessage("§7You joined the " + team.getDisplayName() + " Team");
        }
    }

    @Override
    public ItemStack getItem(Player player) {
        Set<Player> players = team.getOnlinePlayers(game);
        ItemBuilder builder = new ItemBuilder(item).setAmount(Math.max(1, players.size()));
        if (players.isEmpty()) {
            builder.addLore("§cBe the first!");
        } else {
            builder.addLore("§7Players:");
            players.forEach(teamPlayer -> builder.addLore("§7- " + teamPlayer.getName()));
        }
        return builder.build();
    }
}
