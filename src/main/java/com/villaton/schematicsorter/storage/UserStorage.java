package com.villaton.schematicsorter.storage;

import com.villaton.schematicsorter.SchematicSorter;
import de.crafttogether.common.mysql.MySQLConnection;
import de.crafttogether.common.mysql.MySQLConnection.Consumer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserStorage {
    private static final SchematicSorter plugin = SchematicSorter.getInstance();
    private static final HashMap<UUID, User> users = new HashMap<>();

    public static String getCwd(UUID uuid) {
        User user = users.get(uuid);
        return (user == null) ? null : user.getCwd();
    }

    public static void setCwd(UUID uuid, String cwd) {
        setCwd(uuid, cwd, false);
    }

    public static void setCwd(UUID uuid, String cwd, Boolean writeToDatabase) {
        User user = new User(uuid, cwd);

        if (user.getCwd() == null || user.getCwd().isEmpty())
            users.remove(uuid);
        else
            users.put(uuid, user);

        if (writeToDatabase) {

            // Remove entry if cwd == null OR empty string
            if (user.getCwd() == null || user.getCwd().isEmpty()) {
                remove(user, (err, success) -> {
                    if (err != null) {
                        plugin.getLogger().warning("[MySQL]: Error: " + err.getMessage());
                    }
                });
            }

            // Else: Insert or update
            else {
                insertOrUpdate(user, (err, success) -> {
                    if (err != null) {
                        plugin.getLogger().warning("[MySQL]: Error: " + err.getMessage());
                    }
                });
            }
        }
    }

    public static void insertOrUpdate(User user, @Nullable Consumer<SQLException, Boolean> consumer) {
        MySQLConnection connection = plugin.getMySQLAdapter().getConnection();

        connection.queryAsync("SELECT * FROM `%suser` WHERE `uuid` = '" + user.getUuid() + "'", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

                if (consumer != null)
                    consumer.operation(err, false);
            }

            else {
                try {
                    // User found
                    if (!result.next()) {
                        plugin.getLogger().info("[MySQL]: Insert User " + Bukkit.getOfflinePlayer(user.getUuid()).getName());

                        connection.insertAsync("INSERT INTO `%suser` " +
                                        "(" +
                                        "`uuid`, " +
                                        "`cwd` " +
                                        ") " +

                                        "VALUES (" +
                                        "'" + user.getUuid() + "'," +
                                        "'" + user.getCwd() + "'" +
                                        ");",

                                (insertErr, lastInsertId) -> {
                                    if (insertErr != null)
                                        plugin.getLogger().warning("[MySQL]: Error: " + insertErr.getMessage());

                                    if (consumer != null)
                                        consumer.operation(insertErr,insertErr == null);

                                    connection.close();
                                }, connection.getTablePrefix());
                    }

                    // User found! Try to update...
                    else {
                        plugin.getLogger().info("[MySQL]: Update User " + Bukkit.getOfflinePlayer(user.getUuid()).getName());

                        connection.updateAsync("UPDATE `%suser` SET " +
                                        "`uuid` = '" + user.getUuid() + "', " +
                                        "`cwd` = '" + user.getCwd() + "'" +
                                        "WHERE `uuid` = '" + user.getUuid() + "';",

                                (updateErr, affectedRows) -> {
                                    if (updateErr != null)
                                        plugin.getLogger().warning("[MySQL:] Error: " + updateErr.getMessage());

                                    if (consumer != null)
                                        consumer.operation(updateErr, updateErr == null);

                                    connection.close();
                                }, connection.getTablePrefix());
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                    consumer.operation(ex, false);
                }
                finally {
                    connection.close();
                }
            }
        }, connection.getTablePrefix());
    }

    public static void remove(User user, @Nullable Consumer<SQLException, Boolean> consumer) {
        MySQLConnection connection = plugin.getMySQLAdapter().getConnection();

        connection.updateAsync("DELETE FROM `%suser` WHERE `uuid` = '" + user.getUuid() + "';", (err, affectedRows) -> {
            if (err != null)
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

            if (consumer != null)
                consumer.operation(err, err == null);
            connection.close();
        }, connection.getTablePrefix());
    }

    public static void loadAll(@Nullable Consumer<SQLException, Collection<User>> consumer) {
        MySQLConnection connection = plugin.getMySQLAdapter().getConnection();

        connection.queryAsync("SELECT * FROM `%suser`", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

                if (consumer != null)
                    consumer.operation(err, null);
            }

            else {
                try {
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        String cwd = result.getString("cwd");
                        User user = new User(uuid, cwd);

                        // Update cache
                        users.put(user.getUuid(), user);
                    }
                } catch (SQLException ex) {
                    err = ex;
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                }
                finally {
                    connection.close();
                }

                if (consumer != null)
                    consumer.operation(err, users.values());
            }
        }, connection.getTablePrefix());
    }

    public static void load(UUID _uuid, @Nullable Consumer<SQLException, User> consumer) {
        MySQLConnection connection = plugin.getMySQLAdapter().getConnection();

        connection.queryAsync("SELECT * FROM `%suser` WHERE `uuid` = '" + _uuid + "'", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());
                if (consumer != null)
                    consumer.operation(err, null);
            }

            else {
                try {
                    UUID uuid = _uuid;
                    String cwd = null;

                    if (result.next()) {
                        uuid = UUID.fromString(result.getString("uuid"));
                        cwd = result.getString("cwd");
                    }

                    User user = new User(uuid, cwd);

                    // Update cache
                    if (cwd != null)
                        users.put(uuid, user);

                    if (consumer != null)
                        consumer.operation(null, user);
                } catch (SQLException ex) {
                    err = ex;
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                }
                finally {
                    connection.close();
                }
            }
        }, connection.getTablePrefix());
    }
}
