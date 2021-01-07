package i.lovematt.wiisports.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import i.lovematt.wiisports.Core;

import java.io.File;
import java.io.IOException;

public class PlayerFile {

    // TODO: UPDATE IT SOON

    public File file;
    public FileConfiguration config;
    public Player p;

    public PlayerFile(Player p) {
        this.p = p;

        file = new File(Core.getInstance().getDataFolder(), "players/" + p.getUniqueId() + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isExists() {
        return file.exists();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void createNewFile() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();

            config.set("username", p.getName());
            config.set("win", 0);
            config.set("games-played", 0);

            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SETTERS
    public void addWin(int i) {
        config.set("win", getWin() + i);
    }
    public void addGamesPlayed(int i) {
        config.set("games-played", getWin() + i);
    }

    // GETTERS
    public int getWin() {
        return config.getInt("win");
    }

    public int getGamesPlayed() {
        return config.getInt("games-played");
    }

}
