package i.lovematt.wiisports.events;

import i.lovematt.wiisports.Core;
import i.lovematt.wiisports.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropsEvents implements Listener {

    @EventHandler
    public void onItemsDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());

        if (arena != null) {
            if (arena.getPlayers().contains(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }

    }
}
