package com.villaton.schematicsorter.utility;

import org.bukkit.entity.Player;

import java.util.UUID;

public class WorkingDirectory {

    private Player player;
    private final UUID uuid;
    private String path;

// ---------------------------------------- Constructor ----------------------------------------------------------------
    public WorkingDirectory(Player player, WorkingPath working_path) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.path = working_path.getPath();
    }

// ---------------------------------------- Getter-Methods -------------------------------------------------------------
    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPath() {
        return path;
    }

// ---------------------------------------- Setter-Methods -------------------------------------------------------------
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
