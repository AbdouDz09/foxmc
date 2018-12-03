package me.rellynn.foxmc.treewars.shops.items;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWGame;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.game.upgrades.types.MiningSpeedUpgrade;
import me.rellynn.foxmc.treewars.shops.levels.ShopBuyable;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class ShopUpgrade extends ShopBuyable {
    private final TWGame game = TWPlugin.getGame();
    private Upgrade upgrade;

    public ShopUpgrade(Upgrade upgrade) {
        super("upgrade_" + upgrade.getId());
        this.upgrade = upgrade;
    }

    @Override
    public ItemStack getShopItem(Player player) {
        return upgrade.getIcon();
    }

    @Override
    public ShopLevel getLevel(Player player) {
        Team team = game.getArena().getPlayerTeam(player);
        if (team != null) {
            return upgrade.getLevel(game.getTree(team).getUpgradeLevel(upgrade));
        }
        return upgrade.getLevel(0);
    }

    private boolean hasPurchased(Team team) {
        return team != null && game.getTree(team).hasAllLevels(upgrade);
    }

    @Override
    public boolean canPurchase(Player player, ShopLevel level) {
        Team team = game.getArena().getPlayerTeam(player);
        if (team != null && hasPurchased(team)) {
            player.sendMessage("§cYou already unlocked this upgrade!");
            return false;
        }
        return team != null;
    }

    @Override
    public void onPurchase(Player player, ShopLevel level) {
        Team team = game.getArena().getPlayerTeam(player);
        game.getTree(team).addUpgrade(upgrade);
        uglyUpdate(player);
        player.sendMessage("§7You unlocked " + level.getTitle());
        team.getOnlinePlayers(game).forEach(teamPlayer -> {
            teamPlayer.playSound(player.getLocation(), Sound.LEVEL_UP, 0.6F, 1.0F);
            if (teamPlayer != player) {
                teamPlayer.sendMessage("§a" + player.getName() + " §7unlocked " + level.getTitle());
            }
            // Mining speed effect
            if (upgrade == Upgrade.MINING_SPEED)
                teamPlayer.addPotionEffect(MiningSpeedUpgrade.EFFECT);
        });
    }

    @Override
    public ItemStack getItem(Player player) {
        ItemStack item = super.getItem(player);
        if (hasPurchased(game.getArena().getPlayerTeam(player))) {
            return new ItemBuilder(item)
                    .addLore("")
                    .addLore("§aUNLOCKED")
                    .build();
        }
        return item;
    }
}

