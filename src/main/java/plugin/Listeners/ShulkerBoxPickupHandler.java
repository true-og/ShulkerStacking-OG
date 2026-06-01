package plugin.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import plugin.ShulkerBoxHelpers.ShulkerBoxUtils;

public class ShulkerBoxPickupHandler implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void shulkerBoxStacking(EntityPickupItemEvent event) {

        ItemStack item = event.getItem().getItemStack();

        if (!event.getEntity().getType().equals(EntityType.PLAYER)) {

            return;

        }

        if (!ShulkerBoxUtils.isEmptyShulkerBox(item)) {

            return;

        }

        event.setCancelled(true);

        Player player = (Player) event.getEntity();
        Inventory playerInventory = player.getInventory();
        int original = item.getAmount();

        int remaining = original;
        remaining = ShulkerBoxUtils.mergeIntoExistingStacks(playerInventory, item, remaining);
        remaining = ShulkerBoxUtils.fillEmptySlots(playerInventory, item, remaining);

        if (remaining <= 0) {

            event.getItem().remove();
            player.updateInventory();
            return;

        }

        // Preserve entity metadata when the inventory could not accept any items.
        if (remaining < original) {

            ItemStack remainingItem = item.clone();
            remainingItem.setAmount(remaining);
            event.getItem().setItemStack(remainingItem);

        }

        player.updateInventory();

    }

}
