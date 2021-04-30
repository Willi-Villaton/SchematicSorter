package com.villaton.schematicsorter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.storage.Listener.Join_Listener;
import com.villaton.schematicsorter.storage.MySQLAdapter;
import com.villaton.schematicsorter.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SchematicSorter extends JavaPlugin {
    private static SchematicSorter instance;

    private MySQLAdapter mySQLAdapter;

    @Override
    public void onEnable() {
        instance = this;

        // Create config file
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Setup MySQLConfig
        MySQLAdapter.MySQLConfig myCfg = new MySQLAdapter.MySQLConfig();
        myCfg.setHost(config.getString("MySQL.Host"));
        myCfg.setPort(config.getInt("MySQL.Port"));
        myCfg.setUsername(config.getString("MySQL.Username"));
        myCfg.setPassword(config.getString("MySQL.Password"));
        myCfg.setDatabase(config.getString("MySQL.Database"));
        myCfg.setTablePrefix(config.getString("MySQL.TablePrefix"));

        if (!myCfg.checkInputs() || myCfg.getDatabase() == null) {
            getLogger().warning("[MySQL]: Invalid configuration! Please check your config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize MySQLAdapter
        mySQLAdapter = new MySQLAdapter(myCfg);
        MySQLAdapter.MySQLConnection mySQL = mySQLAdapter.getConnection();

        // Create Tables
        try {
            ResultSet result = mySQL.query("SHOW TABLES LIKE '%suser';", mySQL.getTablePrefix());

            if (!result.next()) {
                getLogger().info("[MySQL]: Create Tables ...");

                mySQL.execute(
                        "CREATE TABLE `%suser` (\n" +
                                "  `uuid` varchar(36) NOT NULL,\n" +
                                "  `cwd` varchar(255) NOT NULL\n" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n"
                        , mySQL.getTablePrefix());

                mySQL.execute(
                        "ALTER TABLE `%suser`\n" +
                                "  ADD PRIMARY KEY (`uuid`);"
                        , mySQL.getTablePrefix());
            }
        }
        catch (SQLException ex) {
            getLogger().warning("[MySQL]: " + ex.getMessage());
        }
        finally {
            mySQL.close();
        }

        // Read players from database
        UserStorage.loadAll((err, users) -> {
            if (err != null) {
                getLogger().warning(err.getMessage());
                return;
            }

            getLogger().info(users.size() + " Users loaded");
        });

        // Ready
        getLogger().info("SchematicSorter activated sucessfully.");
        new Join_Listener(this);
        commandRegistration();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("SchematicSorter deactivated sucessfully.");
    }

    private void commandRegistration () {
        getCommand("schems").setExecutor(new Schems());
        getCommand("schems").setTabCompleter(new Schems());
    }

    public MySQLAdapter getMySQLAdapter() {
        return mySQLAdapter;
    }

    public static SchematicSorter getInstance() {
        return instance;
    }
}
