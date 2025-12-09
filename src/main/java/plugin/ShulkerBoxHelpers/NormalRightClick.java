package plugin.ShulkerBoxHelpers;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class NormalRightClick {

    public static void NormalRightClickShulkerBox(ItemStack currentItem, ItemStack cursorItem,
            InventoryClickEvent event)
    {

        // check that both shulker boxes are empty
        // get the inventories of the shulker boxes
        BlockStateMeta shulker1Meta = (BlockStateMeta) currentItem.getItemMeta();
        BlockStateMeta shulker2Meta = (BlockStateMeta) cursorItem.getItemMeta();
        ShulkerBox shulker1 = (ShulkerBox) shulker1Meta.getBlockState();
        ShulkerBox shulker2 = (ShulkerBox) shulker2Meta.getBlockState();
        // check the inventory of each shulker
        if (shulker1.getInventory().isEmpty()) {

            if (shulker2.getInventory().isEmpty()) {

                if (currentItem.getAmount() < 64) {

                    event.getCurrentItem().setAmount(currentItem.getAmount() + 1);
                    int newCursorAmount = cursorItem.getAmount() - 1;
                    if (newCursorAmount <= 0) {

                        event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

                    } else {

                        event.getCursor().setAmount(newCursorAmount);

                    }

                    event.setCancelled(true);

                }

            }

        }

    }

}
