package me.barny1094875.shulkerstackingog.Listeners;

import me.barny1094875.shulkerstackingog.ShulkerBoxHelpers.ShulkerBoxUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShulkerBoxPickupHandler implements Listener {

    @EventHandler
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

        int remaining = item.getAmount();
        remaining = ShulkerBoxUtils.mergeIntoExistingStacks(playerInventory, item, remaining);
        remaining = ShulkerBoxUtils.fillEmptySlots(playerInventory, item, remaining);

        if (remaining <= 0) {

            event.getItem().remove();
            player.updateInventory();
            return;

        }

        ItemStack remainingItem = new ItemStack(item.getType(), remaining);
        event.getItem().setItemStack(remainingItem);
        player.updateInventory();

    }

}
