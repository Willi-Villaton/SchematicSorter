package com.villaton.schematicsorter;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.storage.UserStorage;
import com.villaton.schematicsorter.utility.CPTimer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchematicSorter extends JavaPlugin {

    // --- Private Variables ---
    private static SchematicSorter instance;


    @Override
    public void onEnable() {
        // Plugin startup logic

        //Saving instance
        instance = this;

        switch(startup_sql()) {
            case 0:
                getLogger().info("SQL started and connected successfully.");
                break;
            case 1:
                getLogger().warning("SQL Connection Error.");
                break;
            default:
                getLogger().info("Unidentified error whilst starting SQL-Adapter.");
                break;
        }

        //Load directory storage
        UserStorage.load_user_storage();

        //Start Timer Management
        CPTimer.create_timer_management();

        //Registering all commands
        command_registration();

        //Finished startup
        getLogger().info("SchematicSorter activated successfully.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("SchematicSorter deactivated successfully.");
    }

// ---------------------------------------- Utility-Methods ------------------------------------------------------------
    private void command_registration() {
        getCommand("schems").setExecutor(new Schems());
        getCommand("schems").setTabCompleter(new Schems());
    }

    private int startup_sql() {
        //SQL Startup
        return 0;
    }

// ---------------------------------------- Getter-Methods -------------------------------------------------------------
    public static SchematicSorter getInstance() {
        return instance;
    }
}
