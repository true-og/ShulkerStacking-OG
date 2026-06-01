package plugin.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import plugin.ShulkerBoxHelpers.DoubleClick;
import plugin.ShulkerBoxHelpers.NormalLeftClick;
import plugin.ShulkerBoxHelpers.NormalRightClick;
import plugin.ShulkerBoxHelpers.ShiftRightLeftClick;
import plugin.ShulkerBoxHelpers.ShulkerBoxUtils;

public class ShulkerBoxStackingController implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void shulkerBoxStacking(InventoryClickEvent event) {

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        ClickType click = event.getClick();

        boolean currentIsShulker = ShulkerBoxUtils.isShulkerBoxMaterial(currentItem);
        boolean cursorIsShulker = ShulkerBoxUtils.isShulkerBoxMaterial(cursorItem);

        if (click.equals(ClickType.LEFT) && currentIsShulker && cursorIsShulker
                && currentItem.getType().equals(cursorItem.getType()))
        {

            if (ShulkerBoxUtils.isEmptyShulkerBox(currentItem) && ShulkerBoxUtils.isEmptyShulkerBox(cursorItem)) {

                NormalLeftClick.NormalLeftClickShulkerBox(currentItem, cursorItem, event);

            } else {

                // Filled shulkers cannot use vanilla NBT stacking.
                event.setCancelled(true);

            }

            return;

        }

        if (click.equals(ClickType.RIGHT) && currentIsShulker && cursorIsShulker
                && currentItem.getType().equals(cursorItem.getType()))
        {

            if (ShulkerBoxUtils.isEmptyShulkerBox(currentItem) && ShulkerBoxUtils.isEmptyShulkerBox(cursorItem)) {

                NormalRightClick.NormalRightClickShulkerBox(currentItem, cursorItem, event);

            } else {

                event.setCancelled(true);

            }

            return;

        }

        // Place the whole empty cursor stack into air or a non-shulker slot.
        if (click.equals(ClickType.LEFT) && cursorIsShulker && cursorItem.getAmount() > 1) {

            if (!ShulkerBoxUtils.isEmptyShulkerBox(cursorItem)) {

                return;

            }

            if (currentItem == null || currentItem.getType() == Material.AIR) {

                event.setCurrentItem(cursorItem);
                event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                event.setCancelled(true);

            } else if (!currentIsShulker) {

                event.getWhoClicked().setItemOnCursor(currentItem);
                event.setCurrentItem(cursorItem);
                event.setCancelled(true);

            }

            return;

        }

        if (click.equals(ClickType.DOUBLE_CLICK) && cursorIsShulker) {

            if (ShulkerBoxUtils.isEmptyShulkerBox(cursorItem)) {

                DoubleClick.DoubleClickShulkerBox(cursorItem, event);

            }

            return;

        }

        if ((click.equals(ClickType.SHIFT_LEFT) || click.equals(ClickType.SHIFT_RIGHT)) && currentIsShulker) {

            if (ShulkerBoxUtils.isEmptyShulkerBox(currentItem)) {

                ShiftRightLeftClick.ShiftRightLeftClickShulkerBox(event);

            }

            return;

        }

        // Drop the whole empty stack; vanilla correctly drops filled single boxes.
        if (click.equals(ClickType.CONTROL_DROP) && currentIsShulker && currentItem.getAmount() > 1) {

            if (!ShulkerBoxUtils.isEmptyShulkerBox(currentItem)) {

                return;

            }

            event.setCancelled(true);
            HumanEntity who = event.getWhoClicked();
            Location dropLoc = who.getLocation();
            ItemStack dropStack = currentItem.clone();
            Item dropped = who.getWorld().dropItem(dropLoc, dropStack, (Item entity) -> {

                entity.setVelocity(dropLoc.getDirection().multiply(0.35).setY(0.25));

            });
            // Clear the slot only if ItemSpawnEvent allowed the drop entity.
            if (dropped != null && dropped.isValid() && !dropped.isDead()) {

                event.setCurrentItem(new ItemStack(Material.AIR));

            }

            return;

        }

        // Block creative middle-clone because empty shulkers would be synthesized.
        if (click.equals(ClickType.MIDDLE) && currentIsShulker) {

            event.setCancelled(true);

        }

    }

}
