package i.lovematt.wiisports.file;

import i.lovematt.wiisports.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MsgFile {

    File file;
    FileConfiguration config;

    public MsgFile() {
        file = new File(Core.getInstance().getDataFolder(), "message.yml");

        if (!isExists()) {
            Core.getInstance().saveResource("message.yml", false);
        }

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

    // Players
    public String getJoinMsg(String playerName) {
        return config.getString("player.join").replaceAll("&", "§")
                .replaceAll("%player%", playerName);
    }

    public String getLeftMsg(String playerName) {
        return config.getString("player.left").replaceAll("&", "§")
                .replaceAll("%player%", playerName);
    }

    public String getAlreadyInGameMsg() {
        return config.getString("player.already-ingame").replaceAll("&", "§");
    }

    public String getYouareNotIngameMsg() {
        return config.getString("player.not-ingame").replaceAll("&", "§");
    }

    public String getDeathMsg(String playerName, String killerName) {
        return config.getString("player.death-msg").replaceAll("&", "§")
                .replaceAll("%player%", playerName).replaceAll("%killer%", killerName);
    }

    public String getKitMsg(String kitName) {
        return config.getString("player.kit-select").replaceAll("&", "§")
                .replaceAll("%kit%", kitName);
    }

    public String getNoPermissionsMsg() {
        return config.getString("player.no-permissions").replaceAll("&", "§");
    }

    public List<String> getTop3Msg() {
        return config.getStringList("player.top-3");
    }

    // Arena
    public String getArenaFullMsg() {
        return config.getString("arena.arena-full").replaceAll("&", "§");
    }

    public String getArenaNotSetupMsg() {
        return config.getString("arena.not-setup").replaceAll("&", "§");
    }

    // Game
    public String getGameStartingMsg() {
        return config.getString("game.starting").replaceAll("&", "§");
    }

    public String getStartCountdownMsg(int time) {
        return config.getString("game.start-countdown").replaceAll("&", "§")
                .replaceAll("%time%", time + "");
    }

    public String getEndingMsg(int time) {
        return config.getString("game.ending").replaceAll("&", "§")
                .replaceAll("%time%", time + "");
    }

    public List<String> getGameWinMsg() {
        return config.getStringList("game.win");
    }

    public String getEndingTiedMsg() {
        return config.getString("game.ending-no-winner").replaceAll("&", "§");
    }

    // Admin
    public String getReloadedMsg() {
        return config.getString("admin.reloaded").replaceAll("&", "§");
    }

    public String getArenaCreatedMsg(String arenaName) {
        return config.getString("admin.arena-created").replaceAll("&", "§").replaceAll("%arenaname%", arenaName);
    }

    public String getArenaAlreadyExistsMsg(String arenaName) {
        return config.getString("admin.arena-already-exists").replaceAll("&", "§").replaceAll("%arenaname%", arenaName);
    }

    public String getSetSpawnMsg(String spawn) {
        return config.getString("admin.set-spawn").replaceAll("&", "§").replaceAll("%spawn%", spawn);
    }

    public String getArenaDoesNotExists(String arenaName) {
        return config.getString("admin.arena-not-exists").replaceAll("&", "§").replaceAll("%arenaname%", arenaName);
    }

    public String getArenaRemoved(String arenaName) {
        return config.getString("admin.arena-removed").replaceAll("&", "§").replaceAll("%arenaname%", arenaName);
    }

    public String getLocationNotSet() {
        return config.getString("admin.location-not-set").replaceAll("&", "§");
    }

    public String rewardcmds() {
        return config.getString("reward-commands");
    }

}
