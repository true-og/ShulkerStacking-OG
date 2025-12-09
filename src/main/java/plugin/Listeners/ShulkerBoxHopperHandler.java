package plugin.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import plugin.ShulkerBoxHelpers.ShulkerBoxUtils;

public class ShulkerBoxHopperHandler implements Listener {

    @EventHandler
    public void shulkerBoxHopperHandler(InventoryMoveItemEvent event) {

        ItemStack eventItem = event.getItem();
        Inventory eventDestination = event.getDestination();
        Inventory eventSource = event.getSource();
        if (!ShulkerBoxUtils.isEmptyShulkerBox(eventItem)) {

            return;

        }

        event.setCancelled(true);

        int toMove = eventItem.getAmount();
        for (int i = 0; i < eventSource.getSize() && toMove > 0; i++) {

            ItemStack sourceItem = eventSource.getItem(i);
            if (!ShulkerBoxUtils.isEmptyShulkerBox(sourceItem)) {

                continue;

            }

            if (!sourceItem.getType().equals(eventItem.getType())) {

                continue;

            }

            int moveAmount = Math.min(sourceItem.getAmount(), toMove);
            int remaining = ShulkerBoxUtils.mergeIntoExistingStacks(eventDestination, eventItem, moveAmount);
            remaining = ShulkerBoxUtils.fillEmptySlots(eventDestination, eventItem, remaining);

            int moved = moveAmount - remaining;
            if (moved > 0) {

                sourceItem.setAmount(sourceItem.getAmount() - moved);
                toMove -= moved;
                if (sourceItem.getAmount() <= 0) {

                    eventSource.setItem(i, null);

                }

            }

        }

    }

}
