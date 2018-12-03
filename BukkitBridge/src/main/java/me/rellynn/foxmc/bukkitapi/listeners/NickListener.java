package me.rellynn.foxmc.bukkitapi.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 18/05/2017.
 * FoxMC Network.
 */
public class NickListener implements Listener {
    private static final Map<UUID, Collection<Property>> texturesProps = new HashMap<>();
    private static final Reflection.FieldAccessor<String> profileName = Reflection.getField(GameProfile.class, "name", String.class);
    private static final Reflection.FieldAccessor<String> profilePropertyValue = Reflection.getField(Property.class, "value", String.class);

    public static void updateNickName(Player player, PlayerData data) {
        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        if (data.hasNick()) {
            profileName.set(profile, data.getNickName());
            profile.getProperties().get("textures").forEach(property -> profilePropertyValue.set(property, "random"));
        } else {
            profileName.set(profile, player.getName());
            profile.getProperties().removeAll("textures");
            profile.getProperties().putAll("textures", texturesProps.get(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent evt) {
        Player player = evt.getPlayer();
        texturesProps.put(player.getUniqueId(), ((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures"));
        PlayerData data = FoxAPI.getPlayer(player);
        if (data.hasNick()) {
            updateNickName(player, data);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        texturesProps.remove(evt.getPlayer().getUniqueId());
    }
}
