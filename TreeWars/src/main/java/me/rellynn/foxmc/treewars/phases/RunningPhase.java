package me.rellynn.foxmc.treewars.phases;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWGame;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by gwennaelguich on 29/05/2017.
 * FoxMC Network.
 */
public class RunningPhase extends Phase<TWGame> {
    private int ticks;

    public RunningPhase() {
        super(GameState.RUNNING);
        setTicking(1L);
    }

    @Override
    protected void onStart() {
        game.getArena().getTeams().forEach(team -> {
            Location spawn = team.getPoint("spawn");
            Color color = DyeColor.valueOf(team.getId().toUpperCase()).getColor();
            // Create leather colored armor
            ItemStack[] armor = new ItemStack[]{
                    new ItemBuilder(Material.LEATHER_BOOTS).setUnbreakable(true).setColor(color).build(),
                    new ItemBuilder(Material.LEATHER_LEGGINGS).setUnbreakable(true).setColor(color).build(),
                    null,
                    new ItemBuilder(Material.LEATHER_HELMET).setUnbreakable(true).setColor(color).build()
            };
            team.getOnlinePlayers(game).forEach(player -> {
                player.teleport(spawn);
                player.getInventory().setArmorContents(armor);
                player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                player.setMetadata("PERMANENT_ARMOR", new FixedMetadataValue(TWPlugin.get(), armor));
                player.updateInventory();
            });
        });
        game.getLobby().getBlocks().forEach(block -> block.setType(Material.AIR));
    }

    @Override
    protected void onTick() {
        // Generators
        game.tickGenerators();
        // Teams
        if (ticks % 5 == 0) {
            game.tickTeams();
        }
        // Scoreboards
        if (ticks % 20 == 0) {
            game.updateBoards();
        }
        ticks++;
    }
}
