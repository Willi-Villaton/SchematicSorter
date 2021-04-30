package com.villaton.schematicsorter.storage;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import com.villaton.schematicsorter.SchematicSorter;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserStorage {
    private static HashMap<UUID, User> users = new HashMap<>();

    public static String getCwd(UUID uuid) {
        User user = users.get(uuid);
        return (user == null) ? null : user.getCwd();
    }

    public static void setCwd(UUID uuid, String cwd) {
        setCwd(uuid, cwd, false);
    }

    public static void setCwd(UUID uuid, String cwd, Boolean writeToDatabase) {
        SchematicSorter plugin = SchematicSorter.getInstance();

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
                        return;
                    }
                });
            }

            // Else: Insert or update
            else {
                insertOrUpdate(user, (err, success) -> {
                    if (err != null) {
                        plugin.getLogger().warning("[MySQL]: Error: " + err.getMessage());
                        return;
                    }
                });
            }
        }
    }

    public static void insertOrUpdate(User user, @Nullable Callback<SQLException, Boolean> callback) {
        SchematicSorter plugin = SchematicSorter.getInstance();
        MySQLAdapter.MySQLConnection MySQL = plugin.getMySQLAdapter().getConnection();

        MySQL.queryAsync("SELECT * FROM `%suser` WHERE `uuid` = '" + user.getUuid() + "'", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

                if (callback != null)
                    callback.call(err, false);
            }

            else {
                try {
                    // User found
                    if (!result.next()) {
                        plugin.getLogger().info("[MySQL]: Insert User " + Bukkit.getOfflinePlayer(user.getUuid()).getName());

                        MySQL.insertAsync("INSERT INTO `%suser` " +
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

                                    if (callback != null)
                                        callback.call(insertErr, (insertErr == null) ? true : false);

                                    MySQL.close();
                                }, MySQL.getTablePrefix());
                    }

                    // User found! Try to update...
                    else {
                        plugin.getLogger().info("[MySQL]: Update User " + Bukkit.getOfflinePlayer(user.getUuid()).getName());

                        MySQL.updateAsync("UPDATE `%suser` SET " +
                                        "`uuid` = '" + user.getUuid() + "', " +
                                        "`cwd` = '" + user.getCwd() + "'" +
                                        "WHERE `uuid` = '" + user.getUuid() + "';",

                                (updateErr, affectedRows) -> {
                                    if (updateErr != null)
                                        plugin.getLogger().warning("[MySQL:] Error: " + updateErr.getMessage());

                                    if (callback != null)
                                        callback.call(updateErr, (updateErr == null) ? true : false);

                                    MySQL.close();
                                }, MySQL.getTablePrefix());
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                    callback.call(ex, false);
                }
                finally {
                    MySQL.close();
                }
            }
        }, MySQL.getTablePrefix());
    }

    public static void remove(User user, @Nullable Callback<SQLException, Boolean> callback) {
        SchematicSorter plugin = SchematicSorter.getInstance();
        MySQLAdapter.MySQLConnection MySQL = plugin.getMySQLAdapter().getConnection();

        MySQL.updateAsync("DELETE FROM `%suser` WHERE `uuid` = '" + user.getUuid() + "';", (err, affectedRows) -> {
            if (err != null)
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

            if (callback != null)
                callback.call(err, (err == null) ? true : false);
            MySQL.close();
        }, MySQL.getTablePrefix());
    }

    public static void loadAll(@Nullable Callback<SQLException, Collection<User>> callback) {
        SchematicSorter plugin = SchematicSorter.getInstance();
        MySQLAdapter.MySQLConnection MySQL = plugin.getMySQLAdapter().getConnection();

        MySQL.queryAsync("SELECT * FROM `%suser`", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());

                if (callback != null)
                    callback.call(err, null);
            }

            else {
                try {
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        String cwd = result.getString("cwd");
                        User user = new User(uuid, cwd);

                        // Update cache
                        if (user != null)
                            users.put(user.getUuid(), user);
                    }
                } catch (SQLException ex) {
                    err = ex;
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                }
                finally {
                    MySQL.close();
                }

                if (callback != null)
                    callback.call(err, users.values());
            }
        }, MySQL.getTablePrefix());
    }

    public static void load(UUID _uuid, @Nullable Callback<SQLException, User> callback) {
        SchematicSorter plugin = SchematicSorter.getInstance();
        MySQLAdapter.MySQLConnection MySQL = plugin.getMySQLAdapter().getConnection();

        MySQL.queryAsync("SELECT * FROM `%suser` WHERE `uuid` = '" + _uuid + "'", (err, result) -> {
            if (err != null) {
                plugin.getLogger().warning("[MySQL:] Error: " + err.getMessage());
                if (callback != null)
                    callback.call(err, null);
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

                    if (callback != null)
                        callback.call(null, user);
                } catch (SQLException ex) {
                    err = ex;
                    plugin.getLogger().warning("[MySQL]: Error: " + ex.getMessage());
                }
                finally {
                    MySQL.close();
                }
            }
        }, MySQL.getTablePrefix());
    }
}
