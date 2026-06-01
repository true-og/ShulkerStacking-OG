package plugin.ShulkerBoxHelpers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class DoubleClick {

    public static void DoubleClickShulkerBox(ItemStack cursorItem, InventoryClickEvent event) {

        // Only empty shulkers can be collected.
        Inventory heldShulkerInventory = ((ShulkerBox) ((BlockStateMeta) cursorItem.getItemMeta()).getBlockState())
                .getInventory();
        if (heldShulkerInventory.isEmpty()) {

            // Collect up to 64 matching empty shulkers from both inventories.
            InventoryView eventView = event.getView();
            Inventory playerInventory = eventView.getBottomInventory();
            Inventory containerInventory = eventView.getTopInventory();
            Material shulkerType = cursorItem.getType();
            int containerSize = containerInventory.getSize();

            // Use temporary inventories to skip filled shulkers without removing them.
            Inventory tempPlayerInventory = Bukkit.createInventory(null, 36);
            tempPlayerInventory.setContents(playerInventory.getContents());
            // Use the maximum container size so every view fits.
            Inventory tempContainerInventory = Bukkit.createInventory(null, 54);
            tempContainerInventory.setContents(containerInventory.getContents());

            // Collect empty shulkers from the container first.
            int shulkerBoxCount = cursorItem.getAmount();
            while (tempContainerInventory.first(shulkerType) != -1) {

                int firstShulker = tempContainerInventory.first(shulkerType);
                Inventory shulkerInventory = ((ShulkerBox) ((BlockStateMeta) tempContainerInventory
                        .getItem(firstShulker).getItemMeta()).getBlockState()).getInventory();
                // Skip filled shulkers in the temporary inventory.
                if (!shulkerInventory.isEmpty()) {

                    tempContainerInventory.setItem(firstShulker, new ItemStack(Material.AIR));
                    continue;

                }

                // Result slots terminate the top-inventory scan.
                InventoryType.SlotType slotType = eventView.getSlotType(firstShulker);
                if (slotType.equals(InventoryType.SlotType.RESULT)) {

                    break;

                }

                ItemStack shulkerStackItem = containerInventory.getItem(firstShulker);
                int shulkerStackAmount = shulkerStackItem.getAmount();
                if (shulkerBoxCount + shulkerStackAmount > 64) {

                    int shulkersRequired = 64 - shulkerBoxCount;
                    shulkerStackItem.setAmount(shulkerStackAmount - shulkersRequired);
                    shulkerBoxCount += shulkersRequired;
                    break;

                } else {

                    shulkerBoxCount += shulkerStackAmount;
                    containerInventory.setItem(firstShulker, new ItemStack(Material.AIR));
                    tempContainerInventory.setItem(firstShulker, new ItemStack(Material.AIR));

                }

            }

            // Collect empty shulkers from the player's inventory next.
            while (tempPlayerInventory.first(shulkerType) != -1) {

                int firstShulker = tempPlayerInventory.first(shulkerType);

                Inventory shulkerInventory = ((ShulkerBox) ((BlockStateMeta) tempPlayerInventory.getItem(firstShulker)
                        .getItemMeta()).getBlockState()).getInventory();
                // Skip filled shulkers in the temporary inventory.
                if (!shulkerInventory.isEmpty()) {

                    tempPlayerInventory.setItem(firstShulker, new ItemStack(Material.AIR));
                    continue;

                }

                // Offset by the container size before checking the raw slot type.
                InventoryType.SlotType slotType = eventView.getSlotType(firstShulker + containerSize);
                if (slotType.equals(InventoryType.SlotType.RESULT)) {

                    break;

                }

                ItemStack shulkerStackItem = playerInventory.getItem(firstShulker);
                int shulkerStackAmount = shulkerStackItem.getAmount();
                if (shulkerBoxCount + shulkerStackAmount > 64) {

                    int shulkersRequired = 64 - shulkerBoxCount;
                    shulkerStackItem.setAmount(shulkerStackAmount - shulkersRequired);
                    shulkerBoxCount += shulkersRequired;
                    break;

                } else {

                    shulkerBoxCount += shulkerStackAmount;
                    playerInventory.setItem(firstShulker, new ItemStack(Material.AIR));
                    tempPlayerInventory.setItem(firstShulker, new ItemStack(Material.AIR));

                }

            }

            // Update the cursor only after collecting more than one shulker.
            if (shulkerBoxCount != 1) {

                ItemStack newShulkers = new ItemStack(shulkerType);
                newShulkers.setAmount(shulkerBoxCount);
                event.getWhoClicked().setItemOnCursor(newShulkers);
                event.setCancelled(true);

            }

        }

    }

}
