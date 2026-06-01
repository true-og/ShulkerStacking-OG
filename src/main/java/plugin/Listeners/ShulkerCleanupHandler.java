package plugin.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import plugin.ShulkerStackingOG;

public class ShulkerCleanupHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {

        // Remove the close flag when the player quits to prevent stale entries.
        ShulkerStackingOG.isInventoryClosed.remove(event.getPlayer().getUniqueId());

    }

}
