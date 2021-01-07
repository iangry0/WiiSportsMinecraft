package i.lovematt.wiisports.arena;

import com.google.common.io.ByteStreams;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import i.lovematt.wiisports.Core;
import i.lovematt.wiisports.file.PlayerFile;

import java.io.*;
import java.util.*;

import static org.bukkit.Bukkit.*;

public class Arena {

    public BossBar bar1;
    public BossBar bar2;
    public HashMap<UUID, BossBar> playersBars;

    private int maxPlayers;
    private int matchTime;

    private String name;
    private String winner;

    private Location spawnLoc;
    private Location playerOneLoc;
    private Location playerTwoLoc;

    private GameState state;

    private List<UUID> players;

    private i.lovematt.wiisports.arena.Timer timer;

    private Map<UUID, Location> playerLoc;
    private Map<UUID, ItemStack[]> playerInv;
    private Map<UUID, ItemStack[]> playerArmor;
    private Map<UUID, String> playerKit;

    public Arena( String name, Location spawnLoc, Location playerOne, Location playerTwo, int matchTime) {
        this.name = name;
        this.spawnLoc = spawnLoc;
        this.playerOneLoc = playerOne;
        this.playerTwoLoc = playerTwo;
        this.winner = null;

        maxPlayers = 2;
        this.matchTime = matchTime;

        state = GameState.IDLE;

        players =  new ArrayList<>();
        playerLoc = new HashMap<>();
        playerInv = new HashMap<>();
        playerKit = new HashMap<>();
        playerArmor = new HashMap<>();

        timer = new Timer(this);
        timer.start();
    }

    // Functions
    public void broadcastMessage(String msg) {
        for (int i = 0; i < players.size(); i++) {
            Bukkit.getPlayer(players.get(i)).sendMessage(msg);
        }
    }

    public void userJoin(Player p) {
        if (Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId()) != null) {
            p.sendMessage(Core.getInstance().msgFile.getAlreadyInGameMsg());
            return;
        }

        PlayerFile playerFile = new PlayerFile(p);

        if (!playerFile.isExists()) {
            playerFile.createNewFile();
            playerFile.save();
        }

        if (!Core.getInstance().storageFile.isPlayerExists(p)) {
            Core.getInstance().storageFile.addPlayerUUID(p);
            Core.getInstance().storageFile.save();
        }

        if (!players.contains(p.getUniqueId())) {
            if (spawnLoc != null || playerOneLoc != null || playerTwoLoc != null) {
                if (players.size() < maxPlayers) {

                    players.add(p.getUniqueId());

                    playerInv.put(p.getUniqueId(), p.getInventory().getContents());
                    playerLoc.put(p.getUniqueId(), p.getLocation());
                    playerArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());

                    broadcastMessage(Core.getInstance().msgFile.getJoinMsg(p.getName()));

                    p.getInventory().setArmorContents(null);
                    p.getInventory().clear();
                    p.teleport(spawnLoc);
                    p.setHealth(p.getMaxHealth());
                    p.setFoodLevel(20);

                    Introduction(p);

                    // Add lobby items
                    p.getInventory().addItem(Core.getInstance().createItem(Material.BOW, "§Kit Selector", Arrays.asList("Right-click to select your kit.")));

                    if (players.size() >= maxPlayers) {

                        broadcastMessage(Core.getInstance().msgFile.getGameStartingMsg());
                        setState(GameState.LOBBY);
                    }
                }
                else {
                    p.sendMessage(Core.getInstance().msgFile.getArenaFullMsg());
                }
            }
            else {
                p.sendMessage(Core.getInstance().msgFile.getArenaNotSetupMsg());
            }
        }
        else {
            p.sendMessage(Core.getInstance().msgFile.getAlreadyInGameMsg());
        }
    }

    public void userLeave(Player p) {
        if (players.contains(p.getUniqueId())) {
            if (getState() == GameState.LOBBY) {
                broadcastMessage(Core.getInstance().msgFile.getLeftMsg(p.getName()));
                setState(GameState.IDLE);
                timer.reset();
            }

            if (getState() == GameState.START) {
                p.damage(999);
            }

            if (getState() == GameState.IDLE) {
                broadcastMessage(Core.getInstance().msgFile.getLeftMsg(p.getName()));
            }

            if (playerLoc.containsKey(p.getUniqueId())) {
                p.teleport(playerLoc.get(p.getUniqueId()));
            }

            if (playerInv.containsKey(p.getUniqueId())) {
                p.getInventory().clear();
                p.getInventory().setContents(playerInv.get(p.getUniqueId()));
            }

            if (playerArmor.containsKey(p.getUniqueId())) {
                p.getInventory().setArmorContents(null);
                p.getInventory().setArmorContents(playerArmor.get(p.getUniqueId()));
            }

            playerArmor.remove(p.getUniqueId());
            playerInv.remove(p.getUniqueId());
            playerLoc.remove(p.getUniqueId());
            players.remove(p.getUniqueId());

        }
    }

    public void teleportToArena() {
        Bukkit.getPlayer(players.get(0)).teleport(playerOneLoc);
        Bukkit.getPlayer(players.get(1)).teleport(playerTwoLoc);

        File configFile = new File(Core.getInstance().getDataFolder(), "wiisportsresortswordplayshowdowntheme.nbs");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile(); //Creates new empty file.
                try (InputStream is = Core.getInstance().getResource("wiisportsresortswordplayshowdowntheme.nbs"); OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os); //Copies file from plugin jar into newly created file.
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create wiisportsresortswordplayshowdowntheme.nbs file", e);
            }
        }
        Song song = NBSDecoder.parse(new File(getServer().getWorldContainer() + "/plugins/WiiSports/wiisportsresortswordplayshowdowntheme.nbs"));
// Create PositionSongPlayer.
        PositionSongPlayer psp = new PositionSongPlayer(song);
// Set location where the song will be playing
        psp.setTargetLocation(Bukkit.getPlayer(players.get(0)).getLocation());
        psp.setTargetLocation(Bukkit.getPlayer(players.get(1)).getLocation());
// Set distance from target location in which will players hear the SongPlayer
        psp.setDistance(2); // Default: 16
        psp.setVolume((byte) 100);
// Add player to SongPlayer so he will hear the song.
        psp.addPlayer(Bukkit.getPlayer(players.get(0)));
        psp.addPlayer(Bukkit.getPlayer(players.get(1)));
// Start RadioSongPlayer playback
        psp.setPlaying(true);
        Bukkit.getPlayer(players.get(0)).sendTitle("§6Round 1", "", 1, 40, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> Bukkit.getPlayer(players.get(0)).sendTitle("§6§lFight", "", 1, 80, 1), 30L);
        Bukkit.getPlayer(players.get(0)).playSound(Bukkit.getPlayer(players.get(0)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> Bukkit.getPlayer(players.get(0)).playSound(Bukkit.getPlayer(players.get(0)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 3), 20L);
        Bukkit.getPlayer(players.get(0)).playSound(Bukkit.getPlayer(players.get(0)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 4);
        //p2
        Bukkit.getPlayer(players.get(1)).sendTitle("§6Round 1", "", 1, 40, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> Bukkit.getPlayer(players.get(1)).sendTitle("§6§lFight", "", 1, 80, 1), 30L);
        Bukkit.getPlayer(players.get(1)).playSound(Bukkit.getPlayer(players.get(1)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> Bukkit.getPlayer(players.get(1)).playSound(Bukkit.getPlayer(players.get(1)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 3), 20L);
        Bukkit.getPlayer(players.get(1)).playSound(Bukkit.getPlayer(players.get(1)).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 4);

        //Boss Bar which is displayed to Player 1, this boss bar displays player 2s health
        LivingEntity p1 = Bukkit.getPlayer(players.get(0));
       bar1 = Core.getInstance().getServer().createBossBar("§e" + (Bukkit.getPlayer(players.get(0)).getName()) + " §6§lHealth", BarColor.RED, BarStyle.SOLID);
        bar1.addPlayer(Bukkit.getPlayer(players.get(1)));

            Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), () -> {
                if (!p1.isDead()) {
                    bar1.setProgress(p1.getHealth() / p1.getMaxHealth());
                }
                if (p1.isDead()) {
                    bar1.setProgress(0);
                    bar1.setTitle("§8§lRound Over!");
                }
    }, 0L,1L);
    //Boss Bar which is displayed to Player 2, this boss bar displays player 1s health
    LivingEntity p2 = Bukkit.getPlayer(players.get(1));
    bar2 = Core.getInstance().getServer().createBossBar("§e" + (Bukkit.getPlayer(players.get(1)).getName()) + " §6§lHealth", BarColor.RED, BarStyle.SOLID);
        bar2.addPlayer(Bukkit.getPlayer(players.get(0)));

            Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), () -> {
        if (!p2.isDead()) {
            bar2.setProgress(p2.getHealth() / p2.getMaxHealth());
        }
        if (p2.isDead()) {
            bar2.setProgress(0);
            bar2.setTitle("§8§lRound Over!");
        }
    }, 0L,1L);
}


    public void reset() {
        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));

            if (playerLoc.containsKey(p.getUniqueId())) {
                p.teleport(playerLoc.get(p.getUniqueId()));
            }

            if (playerInv.containsKey(p.getUniqueId())) {
                p.getInventory().clear();
                p.getInventory().setContents(playerInv.get(p.getUniqueId()));
            }

            if (playerArmor.containsKey(p.getUniqueId())) {
                p.getInventory().setArmorContents(null);
                p.getInventory().setArmorContents(playerArmor.get(p.getUniqueId()));
            }
        }
        //resetting scoreboard
        Bukkit.getPlayer(players.get(0)).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Bukkit.getPlayer(players.get(1)).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        //resetting health bossbar
        bar1.removeAll();
        bar2.removeAll();

        players.clear();
        playerLoc.clear();
        playerInv.clear();
        playerArmor.clear();
        playerKit.clear();
        timer.reset();
    }

    public void givePlayerKits() {
        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(getPlayers().get(i));

            p.closeInventory();
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);

            p.setFoodLevel(100);
            p.setHealth(p.getMaxHealth());

            if (playerKit.containsKey(p.getUniqueId())) {
                Core.getInstance().kitFile.givePlayerKit(p, playerKit.get(p.getUniqueId()));
            }
            else {
                Core.getInstance().kitFile.givePlayerKit(p, Core.getInstance().kitFile.getDefaultKits());
            }
        }
    }

    public void addPlayerStats() {
        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));

            if (p.getName() == getWinner()) {
                PlayerFile playerFile = new PlayerFile(p);

                playerFile.addWin(1);
                playerFile.addGamesPlayed(1);
                playerFile.save();

                if (Core.getInstance().storageFile.checkTop3(p, playerFile.getWin())) {
                    Core.getInstance().storageFile.updateLeaderboards(p, playerFile.getWin());
                }
            }
            else {
                PlayerFile playerFile = new PlayerFile(p);

                playerFile.addGamesPlayed(1);
                playerFile.save();
            }
        }
    }

    // Getters
    public String getArenaName() {
        return this.name;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public Map<UUID, String> getPlayerKits() {
        return this.playerKit;
    }

    public Location getSpawn() {
        return this.spawnLoc;
    }

    public GameState getState() {
        return this.state;
    }

    public Location getPlayerOneSpawn() {
        return playerOneLoc;
    }

    public Location getPlayerTwoSpawn() {
        return playerTwoLoc;
    }

    public String getWinner() {
        return  this.winner;
    }

    public int getMatchTime() {
        return this.matchTime;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    // Setters
    public void setState(GameState state) {
        this.state = state;
    }

    public void setSpawn(Location loc) {
        this.spawnLoc = loc;
    }

    public void setPlayerOneSpawn(Location loc) {
        this.playerOneLoc = loc;
    }

    public void setPlayerTwoSpawn(Location loc) {
        this.playerTwoLoc = loc;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void Introduction(Player p){

        p.setScoreboard(Core.getInstance().timerBoard);

        BossBar waiting = Bukkit.createBossBar("§7§lWii §b§lSports §bSwordplay Waiting For Players...", BarColor.BLUE, BarStyle.SOLID);
        waiting.addPlayer(p);

        p.sendTitle("§7§lWii §b§lSports", "§f§lSwordplay Duel", 1, 300, 200);
        p.setGameMode(GameMode.SPECTATOR);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 100));
        File configFile = new File(Core.getInstance().getDataFolder(), "wiisportstheme.nbs");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile(); //Creates new empty file.
                try (InputStream is = Core.getInstance().getResource("wiisportstheme.nbs"); OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os); //Copies file from plugin jar into newly created file.
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create wiisportstheme.nbs file", e);
            }
        }
        Song song = NBSDecoder.parse(new File(getServer().getWorldContainer() + "/plugins/WiiSports/wiisportstheme.nbs"));
// Create PositionSongPlayer.
        PositionSongPlayer psp = new PositionSongPlayer(song);
// Set location where the song will be playing
        psp.setTargetLocation(p.getLocation());
// Set distance from target location in which will players hear the SongPlayer
        psp.setDistance(2); // Default: 16
        psp.setVolume((byte) 100);
// Add player to SongPlayer so he will hear the song.
        psp.addPlayer(p);
// Start RadioSongPlayer playback
        psp.setPlaying(true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> {
            p.setGameMode(GameMode.SURVIVAL);
            waiting.removePlayer(p);
            p.removePotionEffect((PotionEffectType.BLINDNESS));
            psp.setPlaying(false);
            p.sendMessage("§7§lWii §b§lSports");
        }, 380L);
    }

}
