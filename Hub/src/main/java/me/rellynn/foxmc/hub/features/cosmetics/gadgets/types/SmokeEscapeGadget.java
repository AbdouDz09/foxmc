package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
public class SmokeEscapeGadget extends Gadget {
    private static final double RADIUS = 1.2;
    private static final double PARTICLES = 30;
    private static final double INCREMENT = (2 * Math.PI) / PARTICLES;

    public SmokeEscapeGadget() {
        super("smoke_escape", 25, 15);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1400;
        String title = "§6Smoke Escape";
        ItemStack icon = new ItemStack(Material.SULPHUR);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Make your flawless escape", "§7within the cover of smoke!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this gadget"), icon));
        return levels;
    }

    private void hidePlayer(Player player) {
        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }
        HubAPI.get().getPetsManager().removePet(player);
        HubAPI.get().getEffectsManager().stopEffect(player);
        HubAPI.get().getVisibilityManager().setInvisible(player);
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        if (ticksLeft == 290) {
            hidePlayer(player);
            player.sendMessage("§eYou are now invisible!");
            player.getWorld().spigot().playEffect(player.getEyeLocation(), Effect.EXPLOSION_LARGE);
        } else if (ticksLeft > 290) {
            Location location = player.getLocation();
            double y = (double) (300 - ticksLeft) / 5;
            for (int i = 0; i < PARTICLES; i++) {
                double angle = i * INCREMENT;
                double x = RADIUS * Math.cos(angle);
                double z = RADIUS * Math.sin(angle);
                player.getWorld().spigot().playEffect(new Location(player.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z), Effect.LARGE_SMOKE, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
            }
        }
    }

    private void showPlayer(Player player) {
        HubAPI.get().getVisibilityManager().resetState(player);
        HubAPI.get().getPetsManager().showPet(player);
    }

    @Override
    public void onEnd(Player player) {
        showPlayer(player);
        player.sendMessage("§eYou are now visible by other players!");
    }
}
