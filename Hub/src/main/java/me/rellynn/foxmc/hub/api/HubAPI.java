package me.rellynn.foxmc.hub.api;

import lombok.Getter;
import me.rellynn.foxmc.hub.features.cosmetics.effects.EffectsManager;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.GadgetsManager;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.features.cosmetics.pets.PetsManager;
import me.rellynn.foxmc.hub.npcentities.NPCManager;
import me.rellynn.foxmc.hub.visibility.VisibilityManager;
import me.rellynn.foxmc.hub.votecrates.VoteCratesManager;

/**
 * Created by gwennaelguich on 09/04/2017.
 * FoxMC Network.
 */
@Getter
public class HubAPI {
    private static HubAPI hubAPI;

    public static HubAPI get() {
        return hubAPI;
    }

    private VisibilityManager visibilityManager;
    private NPCManager npcManager;
    private GadgetsManager gadgetsManager;
    private PetsManager petsManager;
    private MountsManager mountsManager;
    private EffectsManager effectsManager;
    private VoteCratesManager voteCratesManager;

    public HubAPI() {
        hubAPI = this;
        this.visibilityManager = new VisibilityManager();
        this.npcManager = new NPCManager();
        this.gadgetsManager = new GadgetsManager();
        this.petsManager = new PetsManager();
        this.mountsManager = new MountsManager();
        this.effectsManager = new EffectsManager();
        this.voteCratesManager = new VoteCratesManager();
    }
}
