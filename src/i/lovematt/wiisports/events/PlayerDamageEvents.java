package i.lovematt.wiisports.events;

import i.lovematt.wiisports.Core;
import i.lovematt.wiisports.arena.Arena;
import i.lovematt.wiisports.arena.GameState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerDamageEvents implements Listener {

    public Core plugin;

    public PlayerDamageEvents(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Arena arena = plugin.arenaManager.getPlayersArena(p.getUniqueId());
            if (arena != null) {
                if (arena.getState() == GameState.START) {
                    //make some sort of random system or block edge detection for watch out
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§lYou took damage!"));

                    Player hitter = (Player) e.getDamager();
                    hitter.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§lNice Shot!"));
                }
            }
            if (arena != null) {
                if (arena.getState() == GameState.IDLE || arena.getState() == GameState.END || arena.getState() == GameState.LOBBY) {
                    e.setCancelled(true);
                }
            }
        }
    }
                @EventHandler
                public void onWater(PlayerMoveEvent e) {
                    Player p = e.getPlayer();
                    Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());
                    if (arena != null) {
                        if (arena.getPlayers().contains(p.getUniqueId())) {
                            Material m = e.getPlayer().getLocation().getBlock().getType();
                            if (m == Material.WATER) {
                            // player is in water
                            p.setHealth(0);
                            p.getWorld().playEffect(p.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                            p.sendTitle("§6Round Over", "", 1, 40, 1);
                        }
                    }
                }
            }
}
