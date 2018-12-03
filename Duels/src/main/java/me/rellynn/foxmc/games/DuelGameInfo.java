package me.rellynn.foxmc.games;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.InventoryUtils;
import me.rellynn.foxmc.bukkitapi.utils.PlayerUtils;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.listeners.GameWorldListener;
import me.rellynn.foxmc.games.scheduler.BeginRoundTask;
import me.rellynn.foxmc.games.scheduler.RoundTimer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 01/05/2017.
 * FoxMC Network.
 */
public class DuelGameInfo {
    private static final PotionEffect[] EFFECTS = new PotionEffect[]{
            PotionEffectType.BLINDNESS.createEffect(Integer.MAX_VALUE, 0),
            PotionEffectType.SLOW.createEffect(Integer.MAX_VALUE, 10),
            PotionEffectType.JUMP.createEffect(Integer.MAX_VALUE, 128)
    };

    @Getter private Game game;
    @Getter private ItemStack[] armorContents;
    @Getter private ItemStack[] contents;
    @Getter private Player firstPlayer;
    @Getter private Player secondPlayer;
    private Map<Location, BlockState> blocks = new HashMap<>();
    private List<UUID> roundsWon = new ArrayList<>();
    private RoundTimer roundTimer;

    /**
     * Create a duel with a game.
     *
     * @param game The game
     */
    public DuelGameInfo(Game game) {
        this.game = game;
        try {
            List<String> base64 = (List<String>) game.getSetting(2);
            this.armorContents = InventoryUtils.itemStackArrayFromBase64(base64.get(0));
            this.contents = InventoryUtils.itemStackArrayFromBase64(base64.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Player> iterator = game.getPlayers().iterator();
        this.firstPlayer = iterator.next();
        this.secondPlayer = iterator.next();
        this.roundTimer = new RoundTimer(game, this);
    }

    /**
     * Check if a round is started.
     *
     * @return The result
     */
    public boolean isRoundStarted() {
        return roundTimer != null && roundTimer.isRunning();
    }

    /**
     * Stop the round timer.
     */
    public void stopRoundTimer() {
        if (roundTimer != null) {
            roundTimer.reset();
        }
    }

    /**
     * Start the round timer.
     */
    public void startRoundTimer() {
        if (roundTimer != null) {
            roundTimer.start();
        }
    }

    /**
     * Start a new round.
     * It gives the potion effect to players and reset arena.
     */
    public void startRound() {
        game.getPlayers().forEach(player -> {
            PlayerUtils.resetPlayer(player);
            for (PotionEffect effect : EFFECTS) {
                player.addPotionEffect(effect);
            }
        });
        new BeginRoundTask(this);
    }

    /**
     * Regen arena.
     */
    public void regenBlocks() {
        // Remove entities
        Location lobby = game.getArena().getLobby();
        lobby.getWorld().getEntities().forEach(entity -> {
            if (!(entity instanceof Player) && entity.hasMetadata(GameWorldListener.STORE_META) && game == entity.getMetadata(GameWorldListener.STORE_META).get(0).value())
                entity.remove();
        });
        // Regen arena
        blocks.forEach((location, state) -> {
            Block block = location.getBlock();
            block.removeMetadata(GameWorldListener.STORE_META, DuelsPlugin.get());
            state.update(true);
        });
        blocks.clear();
    }

    /**
     * Add a block to regeneration process.
     *
     * @param state The block state
     */
    public void addBlock(BlockState state) {
        Location location = state.getLocation();
        if (!blocks.containsKey(location)) {
            blocks.put(location, state);
            state.getBlock().setMetadata(GameWorldListener.STORE_META, new FixedMetadataValue(DuelsPlugin.get(), game));
        }
    }

    /**
     * Add a block to regeneration process.
     *
     * @param block The block
     */
    public void addBlock(Block block) {
        addBlock(block.getState());
    }

    /**
     * Remove a block from the regeneration process.
     *
     * @param block The block
     */
    public boolean removeBlock(Block block) {
        BlockState blockState = blocks.remove(block.getLocation());
        if (blockState != null) {
            blockState.getBlock().removeMetadata(GameWorldListener.STORE_META, DuelsPlugin.get());
            return true;
        }
        return false;
    }

    /**
     * Get the player opponent.
     *
     * @param player The player
     * @return The opponent
     */
    public Player getOpponent(Player player) {
        return player == firstPlayer ? secondPlayer : firstPlayer;
    }

    /**
     * Get how many rounds a player won.
     *
     * @param player The player
     * @return Rounds won
     */
    public int getRoundsWon(Player player) {
        return (int) roundsWon.stream().filter(uuid -> uuid == player.getUniqueId()).count();
    }

    /**
     * @return The count of rounds played.
     */
    public int getPlayedRounds() {
        return roundsWon.size();
    }

    /**
     * @return The maximum rounds count
     */
    public int getMaxRounds() {
        return (int) (2 * (double) game.getSetting(0) - 1);
    }

    /**
     * Add a round win to a player.
     *
     * @param player The player
     */
    public void increaseWin(Player player) {
        roundsWon.add(player.getUniqueId());
    }

    /**
     * Check if a player has won the duel.
     *
     * @param player The player
     * @return The result
     */
    public boolean hasWon(Player player) {
        return getRoundsWon(player) == (double) game.getSetting(0);
    }
}
