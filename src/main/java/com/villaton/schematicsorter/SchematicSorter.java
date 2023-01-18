package com.villaton.schematicsorter;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.storage.listener.Join_Listener;
import com.villaton.schematicsorter.storage.UserStorage;
import de.crafttogether.common.dep.net.kyori.adventure.text.Component;
import de.crafttogether.common.dep.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import de.crafttogether.common.mysql.MySQLAdapter;
import de.crafttogether.common.mysql.MySQLConnection;
import de.crafttogether.common.util.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class SchematicSorter extends JavaPlugin {
    private static SchematicSorter instance;

    private MySQLAdapter mySQLAdapter;

    @Override
    public void onEnable() {
        instance = this;

        if (!getServer().getPluginManager().isPluginEnabled("CTCommons")) {
            getLogger().warning("Couldn't find plugin: CTCommons");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create config file
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Initialize MySQLAdapter
        this.mySQLAdapter = new MySQLAdapter(this,
                config.getString("MySQL.Host"),
                config.getInt("MySQL.Port"),
                config.getString("MySQL.Username"),
                config.getString("MySQL.Password"),
                config.getString("MySQL.Database"),
                config.getString("MySQL.TablePrefix"));

        MySQLConnection connection = mySQLAdapter.getConnection();
        if (connection == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Create Tables
        try (ResultSet result = connection.query("SHOW TABLES LIKE '%suser';", connection.getTablePrefix())) {
            if (!result.next()) {
                getLogger().info("[MySQL]: Create Tables ...");

                connection.execute(
                        "CREATE TABLE `%suser` (\n" +
                                "  `uuid` varchar(36) NOT NULL,\n" +
                                "  `cwd` varchar(255) NOT NULL\n" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n"
                        , connection.getTablePrefix());

                connection.execute(
                        "ALTER TABLE `%suser`\n" +
                                "  ADD PRIMARY KEY (`uuid`);"
                        , connection.getTablePrefix());
            }
        }
        catch (SQLException ex) {
            getLogger().warning("[MySQL]: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        finally {
            connection.close();
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
        Objects.requireNonNull(getCommand("schems")).setExecutor(new Schems());
        Objects.requireNonNull(getCommand("schems")).setTabCompleter(new Schems());
    }

    /* Some static methods for simple debugging */
    public static void debug(Player p, String message) {
        debug(p, deserialize(message));
    }

    public static void debug(Player p, Component message) {
        if (!getInstance().getConfig().getBoolean("Debug") || !p.hasPermission("schematicsorter.debug")) return;
        PluginUtil.adventure().player(p).sendMessage(deserialize("&4&l[Debug]: &e").append(message));
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

    public MySQLAdapter getMySQLAdapter() {
        return mySQLAdapter;
    }

    public static SchematicSorter getInstance() {
        return instance;
    }
}
