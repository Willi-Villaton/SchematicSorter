package com.villaton.schematicsorter;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.storage.Listener.Join_Listener;
import com.villaton.schematicsorter.storage.MySQLAdapter;
import com.villaton.schematicsorter.storage.UserStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;

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

        if (mySQL != null) {
            try {
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
                catch (Exception e) {
                    SchematicSorter.getInstance().getLogger().warning("Test 3 ---" + e.getMessage());
                }
//                catch (SQLException ex) {
//                getLogger().warning("[MySQL]: " + ex.getMessage());
//                }
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
            } catch (NullPointerException e) {
                getLogger().warning("[MySQL]: " + "NullPointer Exception whilst establishing!");
            }
        }
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

    /* Some static methods for simple debugging */

    public static void debug(Player p, String message) {
        debug(p, deserialize(message));
    }

    public static void debug(Player p, Component message) {
        if (!getInstance().getConfig().getBoolean("Debug") || !p.hasPermission("schematicsorter.debug")) return;
        p.sendMessage(deserialize("&4&l[Debug]: &e").append(message));
    }

    public static void debug(String message) {
        if (getInstance().getConfig().getBoolean("Debug")) return;
        getInstance().getLogger().info("[Debug]: " + message);
    }

    // Parse string with color codes '&' to component
    public static Component deserialize(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static void performCommand(Player player, String cmd) {
        debug(player, "Run CMD: " + cmd);
        player.performCommand(cmd);
    }

    public static SchematicSorter getInstance() {
        return instance;
    }
}
