package me.rellynn.foxmc.speedbuilders;

import me.rellynn.foxmc.gameapiv2.GameAPIv2;
import me.rellynn.foxmc.gameapiv2.arenas.Arena;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.listeners.*;
import me.rellynn.foxmc.speedbuilders.utils.Build;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
public class SBPlugin extends JavaPlugin {
    private static SBPlugin instance;

    public static SBPlugin get() {
        return instance;
    }

    public static SBGame getGame() {
        return instance.game;
    }

    private SBGame game;
    private List<Build> builds = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        Arena arena = GameAPIv2.get().getArenasManager().pickAvailableArena();
        if (arena == null) {
            getLogger().severe("No arena available! Disabling the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        File buildsFolder = new File(getDataFolder(), "builds");
        if (!buildsFolder.exists()) {
            buildsFolder.mkdirs();
        }
        File[] files = buildsFolder.listFiles(file -> file.getName().endsWith(".schematic"));
        if (files == null || files.length == 0) {
            getLogger().severe("No build available! Disabling the plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Loading " + files.length + " builds...");
        for (File file : files) {
            try {
                builds.add(loadBuild(file));
            } catch (IOException ex) {
                getLogger().severe("Error while loading " + file.getName() + ":");
                ex.printStackTrace();
            }
        }
        game = new SBGame(arena);
        register(new BlockListener());
        register(new EntityListener());
        register(new PlayerListener());
        register(new WorldListener());
        new PacketListener();
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /*
    Builds
     */
    public Build pickBuild() {
        return builds.remove((int) (builds.size() * Math.random()));
    }

    private Build loadBuild(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            NBTTagCompound compound = NBTCompressedStreamTools.a(stream);
            short width = compound.getShort("Width");
            short height = compound.getShort("Height");
            short length = compound.getShort("Length");
            byte[] blocksIds = compound.getByteArray("Blocks");
            byte[] data = compound.getByteArray("Data");
            byte[] addId = new byte[0];
            short[] blocks = new short[blocksIds.length];
            if (compound.hasKey("AddBlocks")) {
                addId = compound.getByteArray("AddBlocks");
            }
            for (int index = 0; index < blocksIds.length; index++) {
                if ((index >> 1) >= addId.length) {
                    blocks[index] = (short) (blocksIds[index] & 0xFF);
                } else if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blocksIds[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blocksIds[index] & 0xFF));
                }
            }
            NBTTagList entitiesList = compound.getList("Entities", 10);
            NBTTagCompound[] entitiesArray = new NBTTagCompound[entitiesList.size()];
            for (int i = 0; i < entitiesList.size(); i++) {
                entitiesArray[i] = entitiesList.get(i);
            }
            Location origin = new Location(null, compound.getInt("WEOriginX"), compound.getInt("WEOriginY"), compound.getInt("WEOriginZ"));
            Build build = new Build(file.getName().replace(".schematic", "").replace("_", " "), width, height, length, blocks, data, entitiesArray, origin);
            build.load();
            return build;
        }
    }
}
