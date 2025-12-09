package plugin.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;

import plugin.ShulkerBoxHelpers.ShulkerBoxUtils;

public class ShulkerBoxMergeHandler implements Listener {

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {

        ItemStack target = event.getTarget().getItemStack();
        ItemStack source = event.getEntity().getItemStack();

        if (!ShulkerBoxUtils.isEmptyShulkerBox(target) || !ShulkerBoxUtils.isEmptyShulkerBox(source)) {

            return;

        }

        if (!target.getType().equals(source.getType())) {

            return;

        }

        event.setCancelled(true);

        int space = 64 - target.getAmount();
        if (space <= 0) {

            return;

        }

        int moved = Math.min(space, source.getAmount());
        target.setAmount(target.getAmount() + moved);
        int remaining = source.getAmount() - moved;

        if (remaining <= 0) {

            event.getEntity().remove();

        } else {

            source.setAmount(remaining);
            event.getEntity().setItemStack(source);

        }

    }

}
