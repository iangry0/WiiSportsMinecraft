package i.lovematt.wiisports.events;

import i.lovematt.wiisports.Core;
import i.lovematt.wiisports.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitEvents implements Listener {

    @EventHandler
    public void onPlayerQuitWhileInGame(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());

        if (arena != null) {
            if (arena.getPlayers().contains(p.getUniqueId())) {
                arena.userLeave(p);
            }
        }

        if (Core.getInstance().playerSettings.containsKey(p)) {
            Core.getInstance().playerSettings.remove(p);
        }
    }

}
