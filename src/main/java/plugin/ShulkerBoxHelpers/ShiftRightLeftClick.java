package plugin.ShulkerBoxHelpers;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class ShiftRightLeftClick {

    public static void ShiftRightLeftClickShulkerBox(InventoryClickEvent event) {

        ItemStack source = event.getCurrentItem();
        if (!ShulkerBoxUtils.isEmptyShulkerBox(source)) {

            return;

        }

        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        Inventory bottom = view.getBottomInventory();
        InventoryType viewType = view.getType();
        Material shulkerType = source.getType();
        int rawSlot = event.getRawSlot();
        int topSize = top.getSize();
        boolean clickedTop = rawSlot < topSize;

        // Cancel default handling before redistributing the stack.
        event.setCancelled(true);

        int remaining = source.getAmount();

        if (viewType.equals(InventoryType.CRAFTING)) {

            // Without a container open, shift within storage slots 0-35 only.
            boolean fromHotbar = event.getSlotType().equals(InventoryType.SlotType.QUICKBAR);
            int destStart = fromHotbar ? 9 : 0;
            int destEnd = fromHotbar ? Math.min(bottom.getSize(), 36) : 9;
            remaining = redistribute(bottom, destStart, destEnd, shulkerType, remaining);

        } else {

            Inventory destination = clickedTop ? bottom : top;
            int destStart = 0;
            // Limit player destinations to storage slots 0-35, including non-vanilla views.
            int destEnd = (destination == bottom) ? Math.min(destination.getSize(), 36) : destination.getSize();
            remaining = redistribute(destination, destStart, destEnd, shulkerType, remaining);

        }

        if (remaining <= 0) {

            event.setCurrentItem(new ItemStack(Material.AIR));

        } else {

            event.setCurrentItem(new ItemStack(shulkerType, remaining));

        }

    }

    private static int redistribute(Inventory inv, int start, int end, Material shulkerType, int amount) {

        // Phase 1: top off existing empty stacks of the same material.
        for (int i = start; i < end && amount > 0; i++) {

            ItemStack slot = inv.getItem(i);
            if (!ShulkerBoxUtils.isEmptyShulkerBox(slot)) {

                continue;

            }

            if (!slot.getType().equals(shulkerType)) {

                continue;

            }

            int space = 64 - slot.getAmount();
            if (space <= 0) {

                continue;

            }

            int moved = Math.min(space, amount);
            slot.setAmount(slot.getAmount() + moved);
            amount -= moved;

        }

        // Phase 2: drop leftovers into AIR slots, one stack at a time.
        for (int i = start; i < end && amount > 0; i++) {

            ItemStack slot = inv.getItem(i);
            if (slot != null && slot.getType() != Material.AIR) {

                continue;

            }

            int placed = Math.min(64, amount);
            inv.setItem(i, new ItemStack(shulkerType, placed));
            amount -= placed;

        }

        return amount;

    }

}
