package com.villaton.schematicsorter.storage;

import com.villaton.schematicsorter.utility.WorkingDirectory;
import com.villaton.schematicsorter.utility.WorkingPath;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.UUID;

public class UserStorage {

    private static LinkedList<WorkingDirectory> users;

// ---------------------------------------- Start-up Methods -----------------------------------------------------------
    public static void load_user_storage() {
        users = new LinkedList<>();
    }

// ---------------------------------------- List Operation Methods -----------------------------------------------------
    public static void update_or_insert(Player player, WorkingPath working_path) {

        if (working_path == null || users == null) {
            return;
        }

        //Checking for already set working dir
        for (WorkingDirectory x : users) {
            if (x.getUuid().equals(player.getUniqueId())) {

                //In case of already set working dir
                users.remove(x);

                WorkingDirectory working_directory = new WorkingDirectory(player, working_path);
                users.add(working_directory);
                return;
            }
        }

        //No Directory set
        WorkingDirectory working_directory = new WorkingDirectory(player, working_path);
        users.add(working_directory);
    }

    public static void remove(Player player) {

        if (player == null || users == null || users.size() <= 0) {
            return;
        }

        for (WorkingDirectory x : users) {
            if (x.getUuid().equals(player.getUniqueId())) {
                users.remove(x);
                return;
            }
        }
    }


// ---------------------------------------- Getter-Methods -------------------------------------------------------------
    public static WorkingDirectory get_specific_working_dir(UUID uuid) {

        if (uuid == null || users == null || users.size() <= 0) {
            return null;
        }

        for (WorkingDirectory x : users) {
            if (x.getUuid().equals(uuid)) {
                //Working Dir found
                return x;
            }
        }

        //No Working Dir found
        return null;
    }

}
