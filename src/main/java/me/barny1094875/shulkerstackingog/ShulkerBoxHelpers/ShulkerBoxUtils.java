package me.barny1094875.shulkerstackingog.ShulkerBoxHelpers;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public final class ShulkerBoxUtils {

    private ShulkerBoxUtils() {

    }

    public static boolean isEmptyShulkerBox(ItemStack itemStack) {

        if (itemStack == null || itemStack.getType() == Material.AIR) {

            return false;

        }

        if (!itemStack.getType().toString().contains("SHULKER_BOX")) {

            return false;

        }

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta)) {

            return false;

        }

        BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
        if (!(meta.getBlockState() instanceof ShulkerBox shulkerBox)) {

            return false;

        }

        return shulkerBox.getInventory().isEmpty();

    }

    public static int mergeIntoExistingStacks(Inventory inventory, ItemStack prototype, int amountToAdd) {

        for (int i = 0; i < inventory.getSize() && amountToAdd > 0; i++) {

            ItemStack slot = inventory.getItem(i);
            if (!isEmptyShulkerBox(slot)) {

                continue;

            }

            if (!slot.getType().equals(prototype.getType())) {

                continue;

            }

            int space = 64 - slot.getAmount();
            if (space <= 0) {

                continue;

            }

            int moved = Math.min(space, amountToAdd);
            slot.setAmount(slot.getAmount() + moved);
            amountToAdd -= moved;

        }

        return amountToAdd;

    }

    public static int fillEmptySlots(Inventory inventory, ItemStack prototype, int amountToAdd) {

        for (int i = 0; i < inventory.getSize() && amountToAdd > 0; i++) {

            ItemStack slot = inventory.getItem(i);
            if (slot != null && slot.getType() != Material.AIR) {

                continue;

            }

            int moved = Math.min(64, amountToAdd);
            inventory.setItem(i, new ItemStack(prototype.getType(), moved));
            amountToAdd -= moved;

        }

        return amountToAdd;

    }

}
