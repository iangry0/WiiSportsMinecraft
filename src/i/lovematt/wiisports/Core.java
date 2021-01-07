package i.lovematt.wiisports;

import i.lovematt.wiisports.arena.Arena;
import i.lovematt.wiisports.commands.WiiCmd;
import i.lovematt.wiisports.events.*;
import i.lovematt.wiisports.file.ArenaFile;
import i.lovematt.wiisports.file.KitFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import i.lovematt.wiisports.arena.ArenaManager;
import i.lovematt.wiisports.file.MsgFile;
import i.lovematt.wiisports.file.StorageFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Core extends JavaPlugin {

    private static Core instance;

    //lobby scorebord countdown
    public Objective o; //Creates a objective called o
    public Scoreboard timerBoard = null; //Creates a scoreboard called timerBoard(You will see what thats used for later)
    public Objective timerObj = null; // Same as above but it creates a objective called timerObj

    public ArenaFile arenaFile;
    public MsgFile msgFile;
    public KitFile kitFile;
    public StorageFile storageFile;
    public ArenaManager arenaManager;

    public Map<Player, Arena> playerSettings = new HashMap<>();

    @Override
    public void onEnable() {
        //lobby scoreboard countdown
        Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        o = board.registerNewObjective("timer", "dummy"); //Registering the objective needed for the timer
        o.setDisplayName("§7§lWii §b§lSports §f§l| §7Swordplay"); // Setting the title for the scoreboard. This would look like: TCGN | Walls
        o.setDisplaySlot(DisplaySlot.SIDEBAR); //Telling the scoreboard where to display when we tell it to display

        this.timerBoard = board; //Setting timerBoard equal to board.
        this.timerObj = o;


        instance = this;


        arenaFile = new ArenaFile();
        msgFile = new MsgFile();
        kitFile = new KitFile();
        storageFile = new StorageFile();

        if (!arenaFile.isFileExists()) {
            arenaFile.createNewFile();
        }

        if (!storageFile.isExists()) {
            storageFile.createNewFile();
        }

        arenaManager = new ArenaManager(this);
        arenaManager.loadAllArena();

        registerCommands();
        registerListeners();

        getLogger().info("Wii Sports Swordplay is now enabled.");


    }

    @Override
    public void onDisable() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            Arena arena = arenaManager.getPlayersArena(p.getUniqueId());

            if (arena != null) {
                if (arena.getPlayers().contains(p.getUniqueId())) {
                    arena.userLeave(p);
                }
            }
        }

        getLogger().info("Wii Sports Swordplay is now disabled.");
    }

    public void registerCommands() {
        getCommand("wiisports").setExecutor(new WiiCmd());
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryEvents(this), this);
        getServer().getPluginManager().registerEvents(new JoinAndQuitEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvents(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageEvents(this), this);
        getServer().getPluginManager().registerEvents(new ItemsInteractEvents(), this);
        getServer().getPluginManager().registerEvents(new ItemDropsEvents(), this);
        getServer().getPluginManager().registerEvents(new FoodEvents(), this);
        getServer().getPluginManager().registerEvents(new CommandProccessEvents(), this);
    }

    public static Core getInstance() {
        return instance;
    }

    public String locationToString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public Location stringToLocation(String s) {
        String[] loc = s.split(":");
        return new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
    }

    public ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

}
