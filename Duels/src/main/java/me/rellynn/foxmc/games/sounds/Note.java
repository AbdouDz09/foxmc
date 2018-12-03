package me.rellynn.foxmc.games.sounds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;

/**
 * Created by gwennaelguich on 07/05/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public class Note {
    private Sound sound;
    private long delay;
    private float pitch;
}
