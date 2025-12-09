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

        ShulkerStackingOG.isInventoryClosed.put(event.getPlayer().getUniqueId(), true);
        Bukkit.getScheduler().runTaskLater(ShulkerStackingOG.getPlugin(),
                () -> ShulkerStackingOG.isInventoryClosed.put(event.getPlayer().getUniqueId(), false), 2);

    }

}