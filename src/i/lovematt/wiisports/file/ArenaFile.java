package i.lovematt.wiisports.file;

import i.lovematt.wiisports.arena.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import i.lovematt.wiisports.Core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static i.lovematt.wiisports.Core.*;

public class ArenaFile {

    public File file;
    public FileConfiguration config;

    public ArenaFile() {
        file = new File(Core.getInstance().getDataFolder(), "arena.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isFileExists() {
        if (file.exists())
            return true;

        return false;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void createNewFile() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            file.createNewFile();

            config.set("arena", new ArrayList<>());

        } catch (IOException e) {
            e.printStackTrace();
        }

        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewArena(String name) {
        config.set("arena." + name + ".name", name);
        config.set("arena." + name + ".match-time", 120);
        config.set("arena." + name + ".spawn", "NONE");
        config.set("arena." + name + ".spawn-one", "NONE");
        config.set("arena." + name + ".spawn-two", "NONE");

        save();
    }

    public void setArenaSpawn(Location loc, Arena arena) {
        String location = getInstance().locationToString(loc);

        config.set("arena." + arena.getArenaName() + ".spawn", location);
    }

    public void setPlayerSpawn(Location loc, Arena arena, int playerSpawn) {
        String location = getInstance().locationToString(loc);

        if (playerSpawn == 1) {
            config.set("arena." + arena.getArenaName() + ".spawn-one", location);
        }
        else {
            config.set("arena." + arena.getArenaName() + ".spawn-two", location);
        }
    }

}

