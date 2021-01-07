package i.lovematt.wiisports.file;

import i.lovematt.wiisports.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StorageFile {

    File file;
    FileConfiguration config;

    public StorageFile() {
        file = new File(Core.getInstance().getDataFolder(), "storage.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isExists() {
        return file.exists();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewFile() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            file.createNewFile();

            config.set("top", Arrays.asList("p1:0", "p2:0", "p3:0"));

            save();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTop3() {
        return config.getStringList("top");
    }

    public boolean checkTop3(Player p, int win) {
        List<String> list = config.getStringList("top");

        if (list.size() > 3) {
            String[] split = list.get(3).split(":");
            int top3wins = Integer.valueOf(split[1]);

            if (top3wins > win) {
                return false;
            }
        }

        return true;
    }

    public void updateLeaderboards(Player p, int win) {
        List<String> list = config.getStringList("top");
        Map<String, Integer> unsortedMap =  new HashMap<>();

        for (String s : list) {
            String[] split = s.split(":");

            if (split[0].toLowerCase().equals(p.getName().toLowerCase())) {
                continue;
            }

            unsortedMap.put(split[0], Integer.valueOf(split[1]));
        }

        list.clear();

        unsortedMap.put(p.getName(), win);

        unsortedMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> list.add(e.getKey() + ":" + e.getValue()));

        if (list.size() >= 4) {
            list.remove(3);
        }

        config.set("top", list);

        save();
    }


    public void addPlayerUUID(Player p) {
        config.set("players." + p.getName(), p.getUniqueId().toString());
    }

    public String getPlayersUUID(Player p) {
        return config.getString("players." + p.getName());
    }

    public boolean isPlayerExists(Player p) {
        return config.getConfigurationSection("players." + p.getName()) == null ? false : true;
    }
}
