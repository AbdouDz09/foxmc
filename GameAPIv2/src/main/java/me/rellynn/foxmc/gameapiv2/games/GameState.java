package me.rellynn.foxmc.gameapiv2.games;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by gwennaelguich on 28/05/2017.
 * FoxMC Network.
 */
@Getter
@AllArgsConstructor
public enum GameState {
    WAITING("§aWaiting ✔"),
    RUNNING("§cPlaying"),
    ENDING("§cRestarting");

    private String name;
}
