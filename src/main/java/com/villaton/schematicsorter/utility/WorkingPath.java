package com.villaton.schematicsorter.utility;

import com.villaton.schematicsorter.storage.UserStorage;
import com.villaton.schematicsorter.ui.UiHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkingPath {

    private static final String WE_PATH_ROOT = "plugins/WorldEdit/schematics";

    private String name;
    private String path;
    private String path_with_root;
    private boolean folder;
    private int sort;
    private File instance;
    private File[] contents;
    private boolean valid;
    private TextComponent[] error;

    public WorkingPath() { }

    // ---------------------------------------- Utility Methods ------------------------------------------------------------
    public static WorkingPath create_path(Player player, String potential_path, boolean check_other_suffix) {
        return create_path(player, potential_path, check_other_suffix, 0);
    }

    public static WorkingPath create_path(File file) {

        WorkingPath working_path = new WorkingPath();
        if (file == null) {
            working_path.setValid(false);
            working_path.setError(UiHandler.invalid_path_error(""));
            return working_path;
        }

        String path = file.getPath().replaceAll("\\\\", "/");
        if (path.startsWith(WE_PATH_ROOT)) {
            path = path.substring(WE_PATH_ROOT.length());
        } else {
            working_path.setValid(false);
            working_path.setError(UiHandler.root_error());
            return working_path;
        }

        path = path.startsWith(" ") ? path.substring(1) : path;
        path = path.startsWith("/") ? path.substring(1) : path;
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        working_path.setPath(path);
        working_path.setPath_with_root(WE_PATH_ROOT + "/" + path);

        validate_path(working_path);

        return working_path;
    }

    public static WorkingPath create_path(Player player, String potential_path, boolean check_other_suffix, int sort) {

        WorkingPath working_path = new WorkingPath();

        if (potential_path == null) {
            working_path.setValid(false);
            working_path.setError(UiHandler.empty_path_error());
            return working_path;
        }

        //Firstly to make pathing possible, path has to be checked
        format_path(player, working_path, potential_path);

        //With a now formatted string validating its path should be possible
        validate_path(working_path);
        if (!working_path.isValid() && !working_path.getPath().endsWith(".schem") && check_other_suffix) {

            working_path.setPath(working_path.path + ".schem");
            working_path.setPath_with_root(working_path.path_with_root + ".schem");
            validate_path(working_path);

            //At this point .schematic support could be added
            if (!working_path.isValid()) {
                return working_path;
            }
        }

        //Finished and move on to sorting
        sort_files_in_folder(working_path, sort);

        return working_path;
    }

    // --- First step ---
    private static void format_path(Player player, WorkingPath working_path, String potential_path) {

        boolean absolute_pathing = false;

        //Correcting path
        if (potential_path != null) {

            //In case of leading space, remove that
            potential_path = potential_path.startsWith(" ") ? potential_path.substring(1) : potential_path;

            //In Case of absolute pathing with working dir
            if (potential_path.startsWith(".")) {
                potential_path = potential_path.substring(1);
                absolute_pathing = true;
            }

            //Remove unnecessary letters such as space and slashes at end and start
            potential_path = potential_path.startsWith(" ") ? potential_path.substring(1) : potential_path;
            potential_path = potential_path.startsWith("/") ? potential_path.substring(1) : potential_path;
            potential_path = potential_path.endsWith("/") ? potential_path.substring(0, potential_path.length() - 1) : potential_path;
        } else {
            //In case if empty path, use root directory
            potential_path = "";
        }

        WorkingDirectory working_directory = UserStorage.get_specific_working_dir(player.getUniqueId());

        if (absolute_pathing || working_directory == null || working_directory.getPath().equals("")) {

            //Path generation with absolute pathing
            working_path.setPath(potential_path);
            working_path.setPath_with_root(WE_PATH_ROOT + "/" + potential_path);

        } else {

            //Path  generation without absolute pathing and with working directory
            String tmp_path = working_directory.getPath() + "/" + potential_path;
            tmp_path = tmp_path.endsWith("/") ? tmp_path.substring(0, tmp_path.length() - 1) : tmp_path;
            //We need the part above because of empty potential_path
            working_path.setPath(tmp_path);
            working_path.setPath_with_root(WE_PATH_ROOT + "/" + tmp_path);
        }
    }

    // --- Second step ---
    private static void validate_path(WorkingPath working_path) {
        try {
            if (working_path == null || working_path.getPath_with_root() == null) {
                return;
            }

            //Loading path
            File file = new File(working_path.getPath_with_root());

            if (!file.exists()) {
                working_path.setValid(false);
                working_path.setError(UiHandler.invalid_path_error(working_path.getPath()));
                return;
            }

            working_path.setValid(true);
            working_path.setName(file.getName());
            working_path.setInstance(file);

            if (file.isDirectory()) {
                working_path.setFolder(true);
                working_path.setContents(file.listFiles());
            } else {
                working_path.setFolder(false);
            }

        } catch (Exception e) {

            working_path.setValid(false);
            working_path.setError(UiHandler.invalid_path_error(working_path.getPath()));
        }
    }

    // --- Third step (optional) ---
    public static void sort_files_in_folder(WorkingPath working_path, int sort) {

        //Sorting only for folders
        if (!working_path.isFolder() && sort != -1) {
            working_path.setValid(false);
            working_path.setError(UiHandler.target_is_file_error());
            return;
        }

        working_path.setSort(sort);

        switch (sort) {
            case -1:
                //With -1 there is no sorting. This is important for single file operations
                break;
            case 0:
                sort_ascending(working_path);
                break;
            case 1:
                sort_descending(working_path);
                break;
            case 2:
                sort_for_smallest(working_path);
                break;
            case 3:
                sort_for_biggest(working_path);
                break;
            case 4:
                sort_for_newest(working_path);
                break;
            case 5:
                sort_for_oldest(working_path);
                break;
            default:
                working_path.setValid(false);
                working_path.setError(UiHandler.sorting_error());
                break;
        }
    }

// ---------------------------------------- Sorting-Utilities ----------------------------------------------------------
    public static void sort_for_newest(WorkingPath working_path) {
        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            long date_f1 = f1.lastModified();
            long date_f2 = f2.lastModified();
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return (int) (date_f1 - date_f2);
            }
        });
    }

    public static void sort_for_oldest(WorkingPath working_path) {
        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            long date_f1 = f1.lastModified();
            long date_f2 = f2.lastModified();
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return (int) (date_f2 - date_f1);
            }
        });
    }

    public static void sort_for_smallest(WorkingPath working_path) {

        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            long size_f1 = FileUtils.sizeOf(f1);
            long size_f2 = FileUtils.sizeOf(f2);
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return (int) (size_f1 - size_f2);
            }
        });
    }

    public static void sort_for_biggest(WorkingPath working_path) {
        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            long size_f1 = FileUtils.sizeOf(f1);
            long size_f2 = FileUtils.sizeOf(f2);
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return (int) (size_f2 - size_f1);
            }
        });
    }

    public static void sort_ascending(WorkingPath working_path) {

        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return f1.compareTo(f2);
            }
        });

    }

    public static void sort_descending(WorkingPath working_path) {
        Arrays.sort(working_path.getContents(), (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return f2.compareTo(f1);
            }
        });
    }

// ---------------------------------------- Other-Utilities ------------------------------------------------------------
    public static WorkingPath step_one_folder_up(WorkingPath working_path) {

        File new_instance;
        if (!working_path.isValid()) {
            //This case only occurs while tab-completion - Needs to reduce string to parent dir
            StringBuilder path = new StringBuilder(working_path.getPath());
            String[] path_parts = path.toString().split("/");
            if (path_parts.length == 1) {
                path.replace(0, path.length(), "");
            } else {
                for (int i = 0; i < path_parts.length - 1; i++) {
                    path.append(path_parts[i]).append("/");
                }
            }
            path.insert(0, WE_PATH_ROOT + "/");
            new_instance = new File(path.toString());

        } else {
            new_instance = working_path.getInstance().getParentFile();
        }

        return create_path(new_instance);
    }

// ---------------------------------------- Getter-Methods -------------------------------------------------------------
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getPath_with_root() {
        return path_with_root;
    }

    public boolean isFolder() {
        return folder;
    }

    public int getSort() {
        return sort;
    }

    public File getInstance() {
        return instance;
    }

    public File[] getContents() {
        return contents;
    }

    public boolean isValid() {
        return valid;
    }

    public TextComponent[] getError() {
        return error;
    }

    public static String get_formatted_date(File file) {
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        return s.format(new Date(file.lastModified()));
    }

    public static String get_formatted_size(File file) {
        if (file.isFile()) {
            return ((int) FileUtils.sizeOf(file) / 1024) + " Kb";
        } else {
            return ((int) FileUtils.sizeOfDirectory(file) / 1024) + " Kb";
        }
    }

    // ---------------------------------------- Setter-Methods -------------------------------------------------------------
    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPath_with_root(String path_with_root) {
        this.path_with_root = path_with_root;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setInstance(File instance) {
        this.instance = instance;
    }

    public void setContents(File[] contents) {
        this.contents = contents;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setError(TextComponent[] error) {
        this.error = error;
    }

}
