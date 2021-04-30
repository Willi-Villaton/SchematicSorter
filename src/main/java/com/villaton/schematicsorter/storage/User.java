package com.villaton.schematicsorter.storage;

import java.util.UUID;

public class User {
    private UUID uuid;
    private String cwd;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.cwd = null;
    }

    public User(UUID uuid, String cwd) {
        this.uuid = uuid;
        this.cwd = cwd;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCwd() {
        return cwd;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }
}
