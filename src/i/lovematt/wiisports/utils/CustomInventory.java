package i.lovematt.wiisports.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import i.lovematt.wiisports.Core;
import i.lovematt.wiisports.arena.Arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomInventory {

    public Core plugin;

    public CustomInventory(Core plugin) {
        this.plugin = plugin;
    }

    public static void openArenaSettings(Player p, Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 27, "Arena: " + arena.getArenaName());

        if (arena.getPlayerOneSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getPlayerOneSpawn());
            inv.setItem(12, Core.getInstance().createItem(Material.ACACIA_DOOR, "§a§lPlayer One Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(12, Core.getInstance().createItem(Material.ACACIA_DOOR, "§c§lPlayer One Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        if (arena.getPlayerTwoSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getPlayerTwoSpawn());
            inv.setItem(13, Core.getInstance().createItem(Material.BIRCH_DOOR, "§a§lPlayer Two Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(13, Core.getInstance().createItem(Material.BIRCH_DOOR, "§c§lPlayer Two Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        if (arena.getSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getSpawn());
            inv.setItem(14, Core.getInstance().createItem(Material.IRON_DOOR, "§a§lLobby or Main Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(14, Core.getInstance().createItem(Material.IRON_DOOR, "§c§lLobby or Main Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        p.openInventory(inv);
    }

    public static void getKitGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, "§7Kit Selector");


        for (String kit : Core.getInstance().kitFile.getConfig().getConfigurationSection("kits").getKeys(false)) {
            String[] items = Core.getInstance().kitFile.getConfig().getString("kits." + kit + ".display").split(":");

            ItemStack item = new ItemStack(Material.valueOf(items[0]), Integer.valueOf(items[1]), Byte.valueOf(items[2]));
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();

            meta.setDisplayName("§7Kit: §a" + kit);

            if (items.length > 3) {
                for (int i = 3; i < items.length; i++) {
                    lore.add(items[i].replace("&", "§"));
                }
            }

            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }

            item.setItemMeta(meta);

            inv.addItem(item);

        }

        p.openInventory(inv);
    }

    public static void getArenaListGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, "§a§lWii Sports");

        for (Arena a : Core.getInstance().arenaManager.getAllArena()) {
            String arenaName = a.getArenaName();
            String arenaState = a.getState().toString();
            int playersJoin = a.getPlayers().size();
            int maxPlayers = a.getMaxPlayers();

            inv.addItem(Core.getInstance().createItem(Material.DIAMOND_CHESTPLATE, "§7Arena: " + arenaName, Arrays.asList("", "§7State: §a" + arenaState, "§7Players: §a" + playersJoin + "/" + maxPlayers, " ", "§7Right-Click to join.")));
        }

        p.openInventory(inv);
    }

}
