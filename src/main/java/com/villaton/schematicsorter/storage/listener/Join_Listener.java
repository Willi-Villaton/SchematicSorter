package com.villaton.schematicsorter.storage.listener;

import com.villaton.schematicsorter.SchematicSorter;
import com.villaton.schematicsorter.storage.UserStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class Join_Listener implements Listener {
    private static Plugin plugin;

    public Join_Listener(SchematicSorter pluginInstance) {
        plugin = pluginInstance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        UserStorage.load(e.getPlayer().getUniqueId(), (err, user) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL]: " + err.getMessage());
                return;
            }

            plugin.getLogger().info("Loaded player " + e.getPlayer().getName());
        });
    }
}
