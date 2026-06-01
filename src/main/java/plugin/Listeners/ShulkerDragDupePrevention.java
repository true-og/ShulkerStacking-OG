package plugin.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import plugin.ShulkerStackingOG;

public class ShulkerDragDupePrevention implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void shulkerDragDupePrevent(InventoryCloseEvent event) {

        java.util.UUID uuid = event.getPlayer().getUniqueId();
        ShulkerStackingOG.isInventoryClosed.put(uuid, true);
        // Reset flags only if present so delayed tasks cannot recreate stale state.
        Bukkit.getScheduler().runTaskLater(ShulkerStackingOG.getPlugin(),
                () -> ShulkerStackingOG.isInventoryClosed.computeIfPresent(uuid, (k, v) -> false), 2);

    }

}
