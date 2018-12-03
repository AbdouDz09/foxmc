package me.rellynn.foxmc.treewars.game.generators;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.gameapiv2.arenas.Team;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.entities.InstantFireworksEntity;
import me.rellynn.foxmc.treewars.game.generators.types.SeedsGenerator;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Created by gwennaelguich on 27/06/2017.
 * FoxMC Network.
 */
class GenUpgradeMenu extends VirtualMenu {

    GenUpgradeMenu(Generator generator) {
        super("generators_" + generator.getId(), 3, "Upgrade Generator");
        // TODO: Cache items
        addItemToDisplay(11, new VirtualItem(id + "_current") {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(generator.getDrop().getItem())
                        .setAmount(generator.getLevel())
                        .setTitle("§6Level " + generator.getLevel())
                        .addLore("§7Spawn time: §b" + String.format("%.2f", generator.getUpgrade().getTicks() / 20.0D) + "s")
                        .build();
            }
        });
        addItemToDisplay(15, new VirtualItem(id + "_next") {
            private ItemStack item;

            private void refreshItem() {
                GeneratorUpgrade upgrade = generator.getNextUpgrade();
                if (upgrade == null) {
                    item = new ItemBuilder(Material.GLASS_BOTTLE)
                            .setTitle("§cFully upgraded!")
                            .addLore("§7This generator has reached the")
                            .addLore("§7maximum level!")
                            .build();
                } else {
                    item = new ItemBuilder(Material.EXP_BOTTLE, generator.getLevel() + 1)
                            .setTitle("§6Level §e" + (generator.getLevel() + 1))
                            .addLore("§7Spawn time: §b" + String.format("%.2f", upgrade.getTicks() / 20.0D) + "s")
                            .addLore("")
                            .addLore("§7Cost: " + upgrade.getCurrency().format(upgrade.getCost()))
                            .build();
                }
            }

            @Override
            protected void onClick(Player player, PerformedAction action) {
                Team team = TWPlugin.getGame().getArena().getPlayerTeam(player);
                if (team == null) {
                    player.sendMessage("§cYou shouldn't see this!");
                    return;
                }
                GeneratorUpgrade upgrade = generator.getNextUpgrade();
                if (upgrade == null) {
                    player.sendMessage("§cThis generator has reached the maximum level!");
                    return;
                } else if (!upgrade.getCurrency().takeItems(player, upgrade.getCost())) {
                    player.sendMessage("§cYou need " + upgrade.getCurrency().format(upgrade.getCost()) + " §cto update the generator to §6level §e" + (generator.getLevel() + 1) + "§7.");
                    return;
                }
                generator.upgrade();
                refreshItem();
                // Messages
                player.closeInventory();
                player.sendMessage("§aGenerator upgraded!");
                team.getOnlinePlayers(TWPlugin.getGame()).forEach(teamPlayer -> {
                    teamPlayer.playSound(player.getLocation(), Sound.LEVEL_UP, 0.6F, 1.0F);
                    if (teamPlayer != player) {
                        teamPlayer.sendMessage("§a" + player.getName() + " §7has upgraded a " + generator.getDrop().getName() + " generator.");
                    }
                });
                // Spawn instant firework
                Location location = generator.getLocation();
                net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
                InstantFireworksEntity fireworksEntity = new InstantFireworksEntity(nmsWorld);
                Firework firework = (Firework) fireworksEntity.getBukkitEntity();
                FireworkMeta meta = firework.getFireworkMeta();
                if (generator instanceof SeedsGenerator) {
                    meta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.LIME).build());
                } else {
                    meta.addEffect(FireworkEffect.builder().withColor(Color.ORANGE).withFade(Color.YELLOW).build());
                }
                firework.setFireworkMeta(meta);
                fireworksEntity.setPosition(location.getX() + 0.5D, location.getY(), location.getZ() + 0.5D);
                nmsWorld.addEntity(fireworksEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

            @Override
            public ItemStack getItem(Player player) {
                if (item == null) {
                    refreshItem();
                }
                return item;
            }
        });
    }
}
