package plugin.Listeners;

import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import plugin.ShulkerBoxHelpers.ShulkerBoxUtils;
import plugin.ShulkerStackingOG;

public class ShulkerDragHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void shulkerBoxDrag(InventoryDragEvent event) {

        ItemStack cursor = event.getOldCursor();
        if (!ShulkerBoxUtils.isShulkerBoxMaterial(cursor)) {

            return;

        }

        // Cancel leaked filled stacks before vanilla can split duplicate NBT.
        if (!ShulkerBoxUtils.isEmptyShulkerBox(cursor)) {

            if (cursor.getAmount() > 1) {

                event.setCancelled(true);

            }

            return;

        }

        event.setCancelled(true);

        final int total = cursor.getAmount();
        if (total < 1) {

            return;

        }

        final Material shulkerType = cursor.getType();
        final DragType dragType = event.getType();
        final Set<Integer> rawSlots = event.getRawSlots();
        final int slotCount = rawSlots.size();
        if (slotCount == 0) {

            return;

        }

        final int perSlot;
        if (dragType.equals(DragType.SINGLE)) {

            perSlot = 1;

        } else if (dragType.equals(DragType.EVEN)) {

            perSlot = total / slotCount;

        } else {

            return;

        }

        if (perSlot <= 0) {

            return;

        }

        final InventoryView view = event.getView();

        Bukkit.getScheduler().runTask(ShulkerStackingOG.getPlugin(), () -> {

            // Skip if inventory just closed: vanilla already ejected cursor contents.
            Boolean closed = ShulkerStackingOG.isInventoryClosed.get(event.getWhoClicked().getUniqueId());
            if (closed != null && closed) {

                return;

            }

            // Abort if vanilla dropped the cursor while the player went offline.
            if (!(event.getWhoClicked() instanceof Player player) || !player.isOnline()) {

                return;

            }

            // Abort if a Q-drop, hotbar swap, or outside-click changed the cursor.
            ItemStack live = player.getItemOnCursor();
            if (!ShulkerBoxUtils.isEmptyShulkerBox(live) || !live.getType().equals(shulkerType)
                    || live.getAmount() < total)
            {

                return;

            }

            // Increment before slot writes so exceptions bias toward loss, not duplication.
            int[] placedRef = new int[] { 0 };
            int[] pendingRef = new int[] { 0 };
            try {

                for (Integer rawSlot : rawSlots) {

                    if (placedRef[0] >= total) {

                        break;

                    }

                    Inventory targetInv = view.getInventory(rawSlot);
                    if (targetInv == null) {

                        continue;

                    }

                    int slotIdx = view.convertSlot(rawSlot);
                    if (slotIdx < 0 || slotIdx >= targetInv.getSize()) {

                        continue;

                    }

                    ItemStack existing = targetInv.getItem(slotIdx);

                    int desired = Math.min(perSlot, total - placedRef[0]);

                    if (existing == null || existing.getType() == Material.AIR) {

                        int toPlace = Math.min(desired, 64);
                        pendingRef[0] = toPlace;
                        placedRef[0] += toPlace;
                        targetInv.setItem(slotIdx, new ItemStack(shulkerType, toPlace));
                        pendingRef[0] = 0;
                        continue;

                    }

                    if (!ShulkerBoxUtils.isEmptyShulkerBox(existing)) {

                        continue;

                    }

                    if (!existing.getType().equals(shulkerType)) {

                        continue;

                    }

                    int space = 64 - existing.getAmount();
                    if (space <= 0) {

                        continue;

                    }

                    int toAdd = Math.min(desired, space);
                    pendingRef[0] = toAdd;
                    placedRef[0] += toAdd;
                    existing.setAmount(existing.getAmount() + toAdd);
                    targetInv.setItem(slotIdx, existing);
                    pendingRef[0] = 0;

                }

            } catch (RuntimeException exception) {

                if (pendingRef[0] > 0) {

                    ShulkerStackingOG.getPlugin().getLogger().log(Level.SEVERE,
                            "Suspected loss of up to " + pendingRef[0] + " " + shulkerType
                                    + " shulker boxes during an inventory drag for " + player.getName() + " ("
                                    + player.getUniqueId() + ").",
                            exception);

                }

                throw exception;

            } finally {

                int leftover = total - placedRef[0];
                if (leftover <= 0) {

                    player.setItemOnCursor(new ItemStack(Material.AIR));

                } else {

                    player.setItemOnCursor(new ItemStack(shulkerType, leftover));

                }

                player.updateInventory();

            }

        });

    }

}
