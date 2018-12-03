package me.rellynn.foxmc.games;

import lombok.Getter;
import me.rellynn.foxmc.api.games.GameType;
import me.rellynn.foxmc.game.api.GameAPI;
import me.rellynn.foxmc.game.api.GameConfig;
import me.rellynn.foxmc.game.api.game.Game;
import me.rellynn.foxmc.games.listeners.GameListener;
import me.rellynn.foxmc.games.listeners.GamePlayerListener;
import me.rellynn.foxmc.games.listeners.GameWorldListener;
import me.rellynn.foxmc.games.sounds.Melody;
import me.rellynn.foxmc.games.sounds.Note;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 25/04/2017.
 * FoxMC Network.
 */
public class DuelsPlugin extends JavaPlugin {
    private static DuelsPlugin plugin;

    public static DuelsPlugin get() {
        return plugin;
    }

    @Getter private Melody winMelody;
    @Getter private Melody defeatMelody;
    private Map<Game, DuelGameInfo> duels = new HashMap<>();

    /*
    EVERYTHING HERE WAS DONE WITH THE FIRST GameAPI
    SO IT HAS TO BE UPDATED WITH THE NEW ONE, GameAPIv2
     */

    @Override
    public void onEnable() {
        plugin = this;
        loadMelodies();
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new GameWorldListener(), this);
        getServer().getPluginManager().registerEvents(new GamePlayerListener(), this);
        GameAPI.get().registerServer(GameType.DUELS, GameConfig.builder().beginCountdown(5).endCountdown(5).build());
    }

    private void loadMelodies() {
        // Win Melody
        winMelody = new Melody();
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 4, 0.8f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 4, 1.6f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 12, 0.8f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 14, 0.8f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 20, 0.7f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 26, 0.6f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 26, 1.18f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 32, 0.67f));
        winMelody.addNote(new Note(Sound.NOTE_PIANO, 38, 0.8f));
        // Loose Melody
        defeatMelody = new Melody();
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 4, 1.18f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 4, 0.7f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 6, 1.18f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 8, 1.18f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 20, 1.34f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 26, 1.42f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 26, 0.8f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 32, 1.34f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 38, 1.18f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 38, 0.7f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 44, 1.06f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 50, 0.94f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 50, 0.6f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 52, 0.94f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 54, 0.94f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 66, 1.18f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 72, 1.78f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 78, 1.6f));
        defeatMelody.addNote(new Note(Sound.NOTE_PIANO, 84, 1.18f));
    }

    public void createDuel(Game game, DuelGameInfo duelInfo) {
        duels.put(game, duelInfo);
    }

    public DuelGameInfo getDuel(Game game) {
        return duels.get(game);
    }

    public DuelGameInfo removeDuel(Game game) {
        return duels.remove(game);
    }
}
