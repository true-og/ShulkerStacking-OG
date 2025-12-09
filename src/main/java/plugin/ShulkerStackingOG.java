package plugin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import plugin.Listeners.ShulkerBoxHopperHandler;
import plugin.Listeners.ShulkerBoxMergeHandler;
import plugin.Listeners.ShulkerBoxPickupHandler;
import plugin.Listeners.ShulkerBoxStackingController;
import plugin.Listeners.ShulkerDragDupePrevention;
import plugin.Listeners.ShulkerDragHandler;

public final class ShulkerStackingOG extends JavaPlugin {

    private static ShulkerStackingOG plugin;

    // Used to prevent duplication glitches from inventory drag events.
    public static HashMap<UUID, Boolean> isInventoryClosed = new HashMap<>();

    @Override
    public void onEnable() {

        plugin = this;

        // Plugin startup logic.
        getServer().getPluginManager().registerEvents(new ShulkerBoxStackingController(), this);
        getServer().getPluginManager().registerEvents(new ShulkerBoxPickupHandler(), this);
        getServer().getPluginManager().registerEvents(new ShulkerDragHandler(), this);
        getServer().getPluginManager().registerEvents(new ShulkerDragDupePrevention(), this);
        getServer().getPluginManager().registerEvents(new ShulkerBoxHopperHandler(), this);
        getServer().getPluginManager().registerEvents(new ShulkerBoxMergeHandler(), this);

    }

    public static ShulkerStackingOG getPlugin() {

        return plugin;

    }

}