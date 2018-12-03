package me.rellynn.foxmc.treewars.game;

import lombok.Getter;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 31/05/2017.
 * FoxMC Network.
 */
public class Tree {
    @Getter private Team team;
    @Getter private Location location;
    private Map<Upgrade, Integer> upgrades = new HashMap<>();

    Tree(Team team) {
        this.team = team;
        location = team.getPoint("tree");
        location.getBlock().setType(Material.BEDROCK);
        location.add(0, 1, 0).getBlock().setType(Material.SAPLING);
    }

    public boolean isThis(Block block) {
        return location.getBlock().equals(block);
    }

    public void destroy() {
        location.getBlock().setType(Material.AIR);
    }

    public boolean isDead() {
        return location.getBlock().getType() != Material.SAPLING;
    }

    public void addUpgrade(Upgrade upgrade) {
        upgrades.put(upgrade, upgrades.getOrDefault(upgrade, 0) + 1);
    }

    public boolean removeUpgrade(Upgrade upgrade) {
        return upgrades.remove(upgrade) != null;
    }

    public boolean hasUpgrade(Upgrade upgrade) {
        return upgrades.containsKey(upgrade);
    }

    public boolean hasAllLevels(Upgrade upgrade) {
        return hasUpgrade(upgrade) && upgrades.get(upgrade) >= upgrade.getLevels();
    }

    public int getUpgradeLevel(Upgrade upgrade) {
        return upgrades.getOrDefault(upgrade, 0);
    }
}
