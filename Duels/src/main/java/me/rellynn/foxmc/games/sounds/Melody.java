package me.rellynn.foxmc.games.sounds;

import me.rellynn.foxmc.games.DuelsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwennaelguich on 07/05/2017.
 * FoxMC Network.
 */
public class Melody {
    private List<Note> notes = new ArrayList<>();
    private long maxDelay;

    public void addNote(Note note) {
        notes.add(note);
        if (note.getDelay() > maxDelay) {
            maxDelay = note.getDelay();
        }
    }

    public void play(Player player) {
        new BukkitRunnable() {
            long delay = 0;

            @Override
            public void run() {
                if (!player.isOnline() || delay > maxDelay) {
                    cancel();
                    return;
                }
                notes.stream().filter(note -> note.getDelay() == delay).forEach(note -> player.playSound(player.getEyeLocation(), note.getSound(), 2, note.getPitch()));
                delay += 2;
            }
        }.runTaskTimer(DuelsPlugin.get(), 0, 2);
    }
}
