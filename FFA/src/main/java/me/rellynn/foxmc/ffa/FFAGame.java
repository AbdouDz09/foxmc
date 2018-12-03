package me.rellynn.foxmc.ffa;

import lombok.Getter;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.ffa.game.FFAMode;
import me.rellynn.foxmc.ffa.game.FFAStats;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.games.GameState;
import me.rellynn.foxmc.gameapiv2.games.phases.Phase;
import me.rellynn.foxmc.gameapiv2.utils.Cuboid;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 05/06/2017.
 * FoxMC Network.
 */
@Getter
public class FFAGame extends Game {
    private Cuboid spawn;
    private ItemStack[] items = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];
    private Map<UUID, FFAStats> stats = new HashMap<>();

    FFAGame(Arena arena) {
        super("ffa", "FFA " + FFAMode.getById(arena.getMode()).getName(), arena);
        spawn = new Cuboid(getArena().getPoint("spawn1"), getArena().getPoint("spawn2"));
        setupItems();
    }

    private void setupItems() {
        // Non-OP items
        if (!isMode(FFAMode.OP)) {
            items[0] = new ItemBuilder(Material.STONE_SWORD).setUnbreakable(true).build();
            items[1] = new ItemBuilder(Material.BOW).setUnbreakable(true).build();
            items[9] = new ItemStack(Material.ARROW, 10);
            armor[0] = new ItemBuilder(Material.IRON_BOOTS).setUnbreakable(true).build();
            armor[1] = new ItemBuilder(Material.IRON_LEGGINGS).setUnbreakable(true).build();
            armor[2] = new ItemBuilder(Material.IRON_CHESTPLATE).setUnbreakable(true).build();
            armor[3] = new ItemBuilder(Material.IRON_HELMET).setUnbreakable(true).build();
        }
        // Shared items
        items[2] = new ItemBuilder(Material.FISHING_ROD).setUnbreakable(true).build();
        items[3] = new ItemBuilder(Material.FLINT_AND_STEEL).setUnbreakable(true).build();
        // Custom items
        switch (getMode()) {
            case UHC:
                items[4] = new ItemStack(Material.GOLDEN_APPLE, 1);
                break;
            case SOUP:
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null)
                        items[i] = new ItemStack(Material.MUSHROOM_SOUP);
                }
                break;
            case OP:
                items[0] = new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 3).setUnbreakable(true).build();
                items[1] = new ItemBuilder(Material.BOW).setUnbreakable(true).addEnchantment(Enchantment.ARROW_INFINITE, 1).build();
                items[4] = new Potion(PotionType.REGEN, 1, false).toItemStack(1);
                items[5] = new Potion(PotionType.STRENGTH, 1, false).toItemStack(1);
                items[6] = new Potion(PotionType.SPEED, 1, false).toItemStack(1);
                items[9] = new ItemStack(Material.ARROW, 1);
                armor[0] = new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).build();
                armor[1] = new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).build();
                armor[2] = new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).build();
                armor[3] = new ItemBuilder(Material.DIAMOND_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).build();
                break;
        }
    }

    @Override
    protected void onLoad() {
        // Always joinable
        addPhase(new Phase(GameState.RUNNING) {

            @Override
            public boolean isJoinable() {
                return getPlayers().size() < getArena().getMaxPlayers();
            }

            @Override
            public boolean canJoin(Player player) {
                return isJoinable() || FoxAPI.getPlayer(player).isAtLeast(Rank.VIP);
            }
        });
    }

    public boolean canBuild(Player player) {
        return player.isOp() && player.getGameMode() == GameMode.CREATIVE;
    }

    public FFAMode getMode() {
        return FFAMode.getById(getArena().getMode());
    }

    public boolean isMode(FFAMode mode) {
        return getMode() == mode;
    }

    /*
    Rewrite statistic methods to implement FFA mode
     */
    @Override
    public void increaseStatistic(Player player, String stat) {
        super.increaseStatistic(player, getMode().getId() + "." + stat);
    }

    @Override
    public int getGivenStatistic(UUID uuid, String stat) {
        return super.getGivenStatistic(uuid, getMode().getId() + "." + stat);
    }
}
