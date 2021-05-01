package com.villaton.schematicsorter.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.storage.UserStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

//TODO: Sorting is messed up. -> Change standart sort from unsorted to alphabetically.

public class Schems implements TabExecutor {

    private final static String WE_PATH = "plugins/WorldEdit/schematics";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(UiHandler.no_player_error());
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("SchematicSorter")) {
            sender.sendMessage(UiHandler.insufficient_permission());
            return false;
        }

        //Check for working dir
        String wo_path = UserStorage.getCwd(player.getUniqueId());

        //No parameters given.
        if (args.length <= 0) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
        } else {
            //Looking for different sub commands.
            switch (args[0]) {
                case "help":
                    command_help(sender, args);
                    break;
                case "list":
                    command_list(sender, args, player, wo_path);
                    break;
                case "cd":
                    command_cd(sender, args, player, wo_path);
                    break;
                case "remove":
                    command_remove(sender, args, wo_path);
                    break;
                case "add":
                    command_add(sender, args, wo_path, false);
                    break;
                case "move":
                    command_move(sender, args, wo_path, player);
                    break;
                case "load":
                    command_load(sender, args, wo_path, player);
                    break;
                case "save":
                    command_save(sender, args, wo_path, player);
                    break;
                case "size":
                    command_size(sender, args, wo_path);
                    break;
                case "copy":
                    command_copy(sender, args, player);
                    break;
                case "paste":
                    command_paste(sender, args, player);
                    break;
                default:
                    sender.sendMessage(UiHandler.wrong_command_error());
                    break;
            }
        }
        return false;
    }

    // ------------------------------ help -----------------------------------------------------------------------------
    private static void command_help(@Nullable CommandSender sender, String[] args) {
        if (args.length <= 1) {
            //General help menu
            sender.sendMessage(UiHandler.help_menu());
        } else {
            if (args.length > 2) {
                sender.sendMessage(UiHandler.too_many_parameters_error());
            } else {
                switch (args[1]) {
                    case "help":
                        sender.sendMessage(UiHandler.help_submenu_help());
                        break;
                    case "list":
                        sender.sendMessage(UiHandler.help_submenu_list());
                        break;
                    case "cd":
                        sender.sendMessage(UiHandler.help_submenu_cd());
                        break;
                    case "remove":
                        sender.sendMessage(UiHandler.help_submenu_remove());
                        break;
                    case "add":
                        sender.sendMessage(UiHandler.help_submenu_add());
                        break;
                    case "move":
                        sender.sendMessage(UiHandler.help_submenu_move());
                        break;
                    case "load":
                        sender.sendMessage(UiHandler.help_submenu_load());
                        break;
                    case "save":
                        sender.sendMessage(UiHandler.help_submenu_save());
                        break;
                    case "size":
                        sender.sendMessage(UiHandler.help_submenu_size());
                        break;
                    case "copy":
                        sender.sendMessage(UiHandler.help_submenu_copy());
                        break;
                    case "paste":
                        sender.sendMessage(UiHandler.help_submenu_paste());
                        break;
                    default:
                        sender.sendMessage(UiHandler.wrong_help_command_error());
                        break;
                }
            }
        }
    }


    // ------------------------------ list -----------------------------------------------------------------------------
    private static void command_list(@Nullable CommandSender sender, String[] args, Player player, String wo_path) {

        LinkedList<String>[] sorted_folder;
        LinkedList<TextComponent[]> output;
        int sort = 0;
        int page;
        String path = "";

        switch (args.length) {
            case 1: //schems list
                //Listing all contents of Working Directory
                sorted_folder = sort_files_in_folder(sender, null, wo_path, 0);
                if (sorted_folder == null) {
                    return;
                }

                //Output to player
                output = UiHandler.list(sorted_folder, 0, sender, sort);
                for (TextComponent[] x : output) {
                    player.spigot().sendMessage(x);
                }
                break;

            case 2://schems list [-noaz/path]

                if (args[1].matches("(\\.?/?(([A-Za-z0-9 -_]+/)+[a-zA-Z0-9 -_]+/?))|(/?([A-Za-z0-9 -_]+/?))") //TODO REGEX
                        && !args[1].equals("-n") && !args[1].equals("-o")
                        && !args[1].equals("-a") && !args[1].equals("-z")) {
                    path = args[1];
                    path = path.matches("[\\.]{2,}") ? "." : path;
                    sorted_folder = sort_files_in_folder(sender, path, wo_path, 0);
                } else {

                    switch (args[1]) {
                        case "-n":
                            sort = 1;
                            break;
                        case "-o":
                            sort = 2;
                            break;
                        case "-a":
                            sort = 3;
                            break;
                        case "-z":
                            sort = 4;
                            break;
                        default:
                            sender.sendMessage(UiHandler.wrong_parameters_error());
                            return;
                    }
                    sorted_folder = sort_files_in_folder(sender, null, wo_path, sort);
                }
                if (sorted_folder == null) {
                    return;
                }
                //Sorted output to player
                output = UiHandler.list(sorted_folder, 0, sender, sort);
                for (TextComponent[] x : output) {
                    player.spigot().sendMessage(x);
                }
                break;

            case 3: //schems list [path/-p] [-noaz]

                //With page argument (Args 1 and 2)
                if (args[1].equals("-p")) {
                    try {
                        page = Integer.parseInt(args[2]) - 1;

                        sorted_folder = sort_files_in_folder(sender, null, wo_path, 0);

                        int total_pages = (sorted_folder[0].size() + sorted_folder[1].size() - 2) / UiHandler.getElementsPerPage();
                        if (page < 0 || page > total_pages) {
                            sender.sendMessage(UiHandler.invalid_page_error(total_pages, page));
                            return;
                        }

                        //Sorted output to player
                        output = UiHandler.list(sorted_folder, page, sender, sort);
                        for (TextComponent[] x : output) {
                            player.spigot().sendMessage(x);
                        }

                    } catch (Exception e) {
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                    }
                    return;
                }

                //With sort and path
                switch (args[2]) {
                    case "-n":
                        sort = 1;
                        break;
                    case "-o":
                        sort = 2;
                        break;
                    case "-a":
                        sort = 3;
                        break;
                    case "-z":
                        sort = 4;
                        break;
                    default:
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                        return;
                }

                path = args[1];
                path = path.matches("[\\.]{2,}") ? "." : path;
                sorted_folder = sort_files_in_folder(sender, path, wo_path, sort);
                if (sorted_folder == null) {
                    return;
                }

                //Output to player
                output = UiHandler.list(sorted_folder, 0, sender, sort);
                for (TextComponent[] x : output) {
                    player.spigot().sendMessage(x);
                }
                break;

            case 4: //schems list [path/-p] [-p] [-noaz]
                //Standard path with -p and -noaz
                page = 0;
                if (args[1].equals("-p")) {
                    try {
                        page = Integer.parseInt(args[2]) - 1;

                    } catch (Exception e) {
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                    }

                    switch (args[3]) {
                        case "-n":
                            sort = 1;
                            break;
                        case "-o":
                            sort = 2;
                            break;
                        case "-a":
                            sort = 3;
                            break;
                        case "-z":
                            sort = 4;
                            break;
                        default:
                            sender.sendMessage(UiHandler.wrong_parameters_error());
                            return;
                    }

                    sorted_folder = sort_files_in_folder(sender, null, wo_path, sort);
                    if (sorted_folder == null) {
                        return;
                    }

                    int total_pages = (sorted_folder[0].size() + sorted_folder[1].size() - 2) / UiHandler.getElementsPerPage();
                    if (page < 0 || page > total_pages) {
                        sender.sendMessage(UiHandler.invalid_page_error(total_pages, page));
                        return;
                    }
                    //Output to player
                    output = UiHandler.list(sorted_folder, page, sender, sort);
                    for (TextComponent[] x : output) {
                        player.spigot().sendMessage(x);
                    }
                } else {

                    //Custom path with -p
                    try {
                        page = Integer.parseInt(args[3]) - 1;

                    } catch (Exception e) {
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                    }

                    path = args[1];
                    path = path.matches("[\\.]{2,}") ? "." : path;
                    sorted_folder = sort_files_in_folder(sender, path, wo_path, 0);
                    if (sorted_folder == null) {
                        return;
                    }

                    int total_pages = (sorted_folder[0].size() + sorted_folder[1].size() - 2) / UiHandler.getElementsPerPage();
                    if (page < 0 || page > total_pages) {
                        sender.sendMessage(UiHandler.invalid_page_error(total_pages, page));
                        return;
                    }

                    //Output to player
                    output = UiHandler.list(sorted_folder, page, sender, sort);
                    for (TextComponent[] x : output) {
                        player.spigot().sendMessage(x);
                    }

                }
                break;

            case 5: //schems [path] [-p] [-noaz]
                page = 0;
                if (args[2].equals("-p")) {
                    try {
                        page = Integer.parseInt(args[3]) - 1;
                    } catch (Exception e) {
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                    }
                } else {
                    sender.sendMessage(UiHandler.wrong_parameters_error());
                }

                switch (args[4]) {
                    case "-n":
                        sort = 1;
                        break;
                    case "-o":
                        sort = 2;
                        break;
                    case "-a":
                        sort = 3;
                        break;
                    case "-z":
                        sort = 4;
                        break;
                    default:
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                        return;
                }

                path = args[1];
                path = path.matches("[\\.]{2,}") ? "." : path;
                sorted_folder = sort_files_in_folder(sender, path, wo_path, sort);
                if (sorted_folder == null) {
                    return;
                }

                int total_pages = (sorted_folder[0].size() + sorted_folder[1].size() - 2) / UiHandler.getElementsPerPage();
                if (page < 0 || page > total_pages) {
                    sender.sendMessage(UiHandler.invalid_page_error(total_pages, page));
                    return;
                }

                //Output to player
                output = UiHandler.list(sorted_folder, page, sender, sort);
                for (TextComponent[] x : output) {
                    player.spigot().sendMessage(x);
                }
                break;

            case 6:
            case 7:
                //Too many parameters error
                sender.sendMessage(UiHandler.too_many_parameters_error());
                return;
            default:
                //Wrong parameters error
                sender.sendMessage(UiHandler.wrong_parameters_error());
        }
    }

    private static LinkedList<String>[] sort_files_in_folder(CommandSender sender, String path, String wo_path, int sort) {
        File folder = validate_file(sender, path, wo_path, false);
        //Validate path
        if (folder == null || !folder.isDirectory()) {
            sender.sendMessage(UiHandler.invalid_path_error());
            return null;
        }

        //Valid file
        LinkedList<String[]> all_contents = new LinkedList<>();
        String[] all_names = folder.list();
        File[] files = folder.listFiles();
        if (files == null) {
            return null;
        }

        //Preparing all data
        LinkedList<String> schematics = new LinkedList<>();
        LinkedList<String> folders = new LinkedList<>();
        LinkedList<String> schematic_dates = new LinkedList<>();
        LinkedList<String> folder_dates= new LinkedList<>();
        LinkedList<String> schematic_sizes = new LinkedList<>();
        LinkedList<String> folder_sizes= new LinkedList<>();
        LinkedList<String> final_path = new LinkedList<>();
        String tmp_path = get_path(path, wo_path, false);
        if (tmp_path.equals(".")) { //If standard folder given twice the dot needed to be replaced.
            final_path.add("");
        } else {
            final_path.add(tmp_path);
        }

        //On wish, standard sort is alphabetically ascending
        if (sort == 0) {
            sort = 3;
        }
        switch (sort) {
            case 0:
                all_contents.add(all_names);
                String[] dates = new String[files.length];
                String[] sizes = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
                    dates[i] = s.format(new Date(files[i].lastModified()));
                    sizes[i] = get_size(files[i]) + " Kb";
                }
                all_contents.add(dates);
                all_contents.add(sizes);
                break;
            case 1:
                all_contents = sort_for_newest(files);
                break;
            case 2:
                all_contents = sort_for_oldest(files);
                break;
            case 3:
                all_contents = sort_ascending(all_names, files);
                break;
            case 4:
                all_contents = sort_descending(all_names, files);
                break;
            default:
                sender.sendMessage(UiHandler.unexpected_error(210));
                return null;

        }
        for (int i = 0; i < all_contents.get(0).length; i++) {
            String current_element = all_contents.get(0)[i];
            if (current_element.matches("([A-Za-z0-9 -_]+\\.schematic)|([A-Za-z0-9 -_]+\\.schem)")) {
                current_element = current_element.split(".schem")[0];
                schematics.add(current_element);
                schematic_dates.add(all_contents.get(1)[i]);
                schematic_sizes.add(all_contents.get(2)[i]);
                continue;
            }
            if (!current_element.matches("([A-Za-z0-9 -_]+\\.[a-zA-Z0-9 -_]+)")) {
                folders.add(current_element);
                folder_dates.add(all_contents.get(1)[i]);
                folder_sizes.add(all_contents.get(2)[i]);
            }
        }

        LinkedList<String>[] output = new LinkedList[7];
        output[0] = schematics;
        output[1] = folders;
        output[2] = schematic_dates;
        output[3] = folder_dates;
        output[4] = schematic_sizes;
        output[5] = folder_sizes;
        output[6] = final_path;
        return output;
    }

    public static String get_path(String path, String wo_path, boolean with_standard){

        //Correcting path
        if (path != null) {
            //In Case of absolute pathing with working dir
            if (path.startsWith(".") && wo_path != null) {
                wo_path = null;
                path = path.substring(1);
            }
            path = path.startsWith(" ") ? path.substring(1) : path;
            path = path.startsWith("/") ? path.substring(1) : path;
            path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        } else {
            path = "";
        }

        //Generate path
        if (wo_path != null) {
            wo_path = wo_path.startsWith("/") ? wo_path.substring(1) : wo_path;
            wo_path = wo_path.endsWith("/") ? wo_path.substring(0, wo_path.length() - 1) : wo_path;

            if (with_standard) {
                path = WE_PATH + "/" + wo_path + "/" + path;
            } else {
                path = wo_path + "/" + path;
            }
        } else {
            if (with_standard) {
                path = WE_PATH + "/" + path;
            }
        }
        return path;
    }

    public static String one_folder_back(@Nullable CommandSender sender, String path) {
        //Hop back one folder
        String[] tmp_path = path.split("/");
        path = "";
        switch (tmp_path.length) {
            case 0:
                sender.sendMessage(UiHandler.unexpected_error(202));
                return null;
            case 1:
                break;
            case 2:
                path = tmp_path[0];
                break;
            default:
                for (int i = 0; i < tmp_path.length - 2; i++) {
                    path += tmp_path[i] + "/";
                }
                path += tmp_path[tmp_path.length - 2];
        }

        //Checking the new directory for errors
        if (validate_file(sender, path, null, true) == null) {
            return null;
        }
        return path;
    }

    private static File validate_file(@Nullable CommandSender sender, String path, String wo_path, boolean suppress_error) {
        try {
            path = get_path(path, wo_path, true);
            if (path == null) {
                sender.sendMessage(UiHandler.unexpected_error(215));
                return null;
            }

            //Loading path
            File file = new File(path);

            if (!file.exists()) {
                if (!suppress_error) {
                    sender.sendMessage(UiHandler.invalid_path_error());
                }
                return null;
            }
            return file;
        } catch (Exception e) {
            if (!suppress_error) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "Huhuu. Sag mal Villaton bitte was du angestellt hast.");
                sender.sendMessage(UiHandler.invalid_path_error());
            }
            return null;
        }
    }

    private static LinkedList<String[]> sort_for_newest(File[] files) {
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        String[] raw_list = new String[files.length];
        String[] dates = new String[files.length];
        String[] sizes = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            raw_list[i] = files[i].getName();
            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
            dates[i] = s.format(new Date(files[i].lastModified()));
            sizes[i] = get_size(files[i]) + " Kb";
        }

        LinkedList<String[]> ret = new LinkedList<>();
        ret.add(raw_list);
        ret.add(dates);
        ret.add(sizes);

        return ret;
    }

    private static LinkedList<String[]> sort_for_oldest(File[] files) {
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        String[] raw_list = new String[files.length];
        String[] dates = new String[files.length];
        String[] sizes = new String[files.length];
        for (int o = 0, i = files.length; i > 0; i--, o++) {
            raw_list[o] = files[i - 1].getName();
            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
            dates[o] = s.format(new Date(files[i - 1].lastModified()));
            sizes[o] = get_size(files[i - 1]) + " Kb";
        }

        LinkedList<String[]> ret = new LinkedList<>();
        ret.add(raw_list);
        ret.add(dates);
        ret.add(sizes);

        return ret;
    }

    private static LinkedList<String[]> sort_ascending(String[] raw_list, File[] files) {

        LinkedList<String> raw = new LinkedList<>();
        Collections.addAll(raw, raw_list);

        Collections.sort(raw, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return extractInt(o1) -  extractInt(o2);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        String[] dates = new String[raw.size()];
        String[] sizes = new String[files.length];
        for (int i = 0; i < raw.size(); i++) {
            raw_list[i] = raw.get(i);
            for (int j = 0; j < raw.size(); j++) {
                if (raw_list[i].equals(files[j].getName())) {
                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
                    dates[i] = s.format(new Date(files[j].lastModified()));
                    sizes[i] = get_size(files[j]) + " Kb";
                }
            }

        }

        LinkedList<String[]> ret = new LinkedList<>();
        ret.add(raw_list);
        ret.add(dates);
        ret.add(sizes);

        return ret;
    }

    private static LinkedList<String[]> sort_descending(String[] raw_list, File[] files) {

        LinkedList<String> raw = new LinkedList<>(Arrays.asList(raw_list));

        Collections.sort(raw, new Comparator<String>() { //TODO Komischer Fehler
            public int compare(String o1, String o2) {
                return extractInt(o2) - extractInt(o1);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        String[] dates = new String[raw.size()];
        String[] sizes = new String[files.length];
        for (int i = 0; i < raw.size(); i++) {
            raw_list[i] = raw.get(i);
            for (int j = 0; j < raw.size(); j++) {
                if (raw_list[i].equals(files[j].getName())) {
                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
                    dates[i] = s.format(new Date(files[j].lastModified()));
                    sizes[i] = get_size(files[i]) + " Kb";
                    break;
                }
            }
        }

        LinkedList<String[]> ret = new LinkedList<>();
        ret.add(raw_list);
        ret.add(dates);
        ret.add(sizes);

        return ret;
    }


    // ------------------------------ cd -------------------------------------------------------------------------------
    private static void command_cd(@Nullable CommandSender sender, String[] args, Player player, String wo_path) {

        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
            return;
        }
        if (args.length == 1) {
            if (wo_path == null || wo_path == "") {
                sender.sendMessage(UiHandler.cd_display("Not set - Standard path"));
            } else {
                sender.sendMessage(UiHandler.cd_display(wo_path));
            }
            return;
        }
        switch (args[1]) {
            case "..":
                if (wo_path != null) {

                    //If somehow there is a wrong folder
                    if (wo_path.equals(WE_PATH) || wo_path.equals("")) {
                        UserStorage.setCwd(player.getUniqueId(), null, true); // Setting cwd to null removes entry from db
                        sender.sendMessage(UiHandler.exit_standard_folder_error());
                        return;
                    }

                    wo_path = one_folder_back(sender, wo_path);
                    if (wo_path == null) {
                        return;
                    }

                    //Saving new directory
                    if (wo_path.equals(WE_PATH)  || wo_path.equals("")) {
                        UserStorage.setCwd(player.getUniqueId(), null, true);
                        sender.sendMessage(UiHandler.cd("Standard folder"));
                    } else {
                        UserStorage.setCwd(player.getUniqueId(), wo_path, true);
                        sender.sendMessage(UiHandler.cd(wo_path));
                    }
                } else {
                    sender.sendMessage(UiHandler.exit_standard_folder_error());
                    return;
                }
                break;
            case "-s":
                //Saving new directory
                UserStorage.setCwd(player.getUniqueId(), null, true); //TODO IST DAS SCHLAU?
                sender.sendMessage(UiHandler.cd("Standard folder"));
                break;
            default:
                //Operating relative to old working dir
                if (args[1].startsWith(".")) {

                    if (wo_path == null) {
                        sender.sendMessage(UiHandler.wrong_parameters_error());
                        return;
                    }

                    String path = args[1].startsWith(".") ? args[1].substring(1) : args[1];
                    wo_path = "";

                    //Validate path
                    File file = validate_file(sender, path, null, false);
                    if (file == null || !file.isDirectory()) {
                        sender.sendMessage(UiHandler.invalid_path_error());
                        return;
                    }

                    //Update directory one
                    UserStorage.setCwd(player.getUniqueId(), path, true); //TODO IST DAS SCHLAU?
                    sender.sendMessage(UiHandler.cd(path));
                    return;
                } else {
                    //Operation relative to standard folder
                    if (wo_path == null) {
                        wo_path = "";
                    }

                    String path = args[1];

                    //Validate path
                    File file = validate_file(sender, path, wo_path, false);
                    if (file == null || !file.isDirectory()) {
                        sender.sendMessage(UiHandler.invalid_path_error());
                        return;
                    }

                    //Update directory one
                    String new_path = wo_path + "/" + path;
                    UserStorage.setCwd(player.getUniqueId(), new_path, true); //TODO IST DAS SCHLAU?
                    sender.sendMessage(UiHandler.cd(new_path));
                    return;
                }
        }
    }


    // ------------------------------ Remove ---------------------------------------------------------------------------
    private static void command_remove(@Nullable CommandSender sender, String[] args, String wo_path) {

        //Checking for wrong parameters
        if (args.length > 3) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
            return;
        }
        if (args.length <= 1) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
            return;
        }

        boolean no_question;
        if (args.length == 2) {
            no_question = false;
        } else {
            if (args[2].equals("-y")) {
                no_question = true;
            } else {
                sender.sendMessage(UiHandler.wrong_parameters_error());
                return;
            }
        }

        String file_path = args[1];
        File file  = validate_file(sender, file_path, wo_path, true);
        if (file == null) {
            file_path = file_path.endsWith(".schem") ? file_path + ".schem" : file_path;
            file = validate_file(sender, file_path, wo_path, true);
            if (file != null) {
                sender.sendMessage(UiHandler.remove_other_name(file_path));
            } else {
                file_path = file_path.endsWith(".schem") ? file_path.substring(0, file_path.length() - 6) : file_path;
                file_path = !file_path.endsWith(".schematic") ? file_path : file_path + ".schematic" ;
                file = validate_file(sender, file_path, wo_path, false);
                if (file != null) {
                    sender.sendMessage(UiHandler.remove_other_name(file_path));
                }
            }
            return;
        }

        if (!no_question) {
            sender.sendMessage(UiHandler.remove(file.getName()));
        } else {
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    sender.sendMessage(UiHandler.unexpected_error(220));
                }
                sender.sendMessage(UiHandler.deleted());
            } else {
                file.delete();
                sender.sendMessage(UiHandler.deleted());
            }
        }
    }


    // ------------------------------ Add ------------------------------------------------------------------------------
    private static void command_add(@Nullable CommandSender sender, String[] args, String wo_path, boolean suppress_output) {
        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
            return;
        }

        File file = validate_file(sender, args[1], wo_path, true);
        if (file != null && file.exists()) {
            sender.sendMessage(UiHandler.folder_already_exists_error(get_path(args[1], wo_path, false)));
            return;
        } else {
            new File(get_path(args[1], wo_path, true)).mkdirs();
        }
        if (!suppress_output) {
            sender.sendMessage(UiHandler.added_folder(args[1], get_path(args[1], wo_path, false)));
        }
    }


    // ------------------------------ Move -----------------------------------------------------------------------------
    private static void command_move(@Nullable CommandSender sender, String[] args, String wo_path, Player player) {
        if (args.length > 3) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
        }
        if (args.length < 3) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
        }

        String file_path = args[1];
        File file  = validate_file(sender, file_path, wo_path, true);
        if (file == null) {
            file_path = !file_path.endsWith(".schem") ? file_path + ".schem" : file_path;
            file = validate_file(sender, file_path, wo_path, true);
            if (file != null) {
                file_path = file_path.endsWith(".schem") ? file_path.substring(0, file_path.length() - 6) : file_path;
                file_path = file_path.endsWith(".schematic") ? file_path : file_path + ".schematic" ;
                sender.sendMessage(UiHandler.move_other_name( file_path,
                        get_path(args[2], wo_path,false)));
            } else {
                file = validate_file(sender, file_path + ".schematic", wo_path, false);
                if (file != null) {
                    sender.sendMessage(UiHandler.move_other_name(file_path,
                            get_path(args[2], wo_path,false)));

                }
            }
            return;
        }

        File target_folder = validate_file(sender, args[2], wo_path, false);
        if (target_folder == null) {
            command_add(sender, new String[]{"add",args[2]}, wo_path, true);
            target_folder = validate_file(sender, args[2], wo_path, false);
            if (target_folder == null) {
                sender.sendMessage(UiHandler.unexpected_error(222));
                return;
            }
        }

        String[] tmp_path = file_path.split("/");
        String name = tmp_path[tmp_path.length - 1];

        file.renameTo(new File(get_path(args[2] + "/" + name, wo_path, true)));
        sender.sendMessage(UiHandler.moved_file(get_path(args[2], wo_path, false)));
        command_list(sender, new String[]{"list", get_path(args[2], wo_path, false),"-n"}, player, null);
    }


    // ------------------------------ Load -----------------------------------------------------------------------------
    private static void command_load(@Nullable CommandSender sender, String[] args, String wo_path, Player player) {
        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
        }
        if (args.length < 2) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
        }

        String file_path = args[1];
        File file  = validate_file(sender, args[1], wo_path, true);
        if (file == null) {
            file_path = !file_path.endsWith(".schem") ? file_path + ".schem" : file_path;
            file = validate_file(sender, file_path, wo_path, true);
            if (file != null) {
                sender.sendMessage(UiHandler.load_other_name(file_path));
            } else {
                file_path = file_path.endsWith(".schem") ? file_path.substring(0, file_path.length() - 6) : file_path;
                file_path = file_path.endsWith(".schematic") ? file_path : file_path + ".schematic" ;
                file = validate_file(sender, file_path, wo_path, false);
                if (file != null) {
                    sender.sendMessage(UiHandler.load_other_name(file_path));
                }
            }
            return;
        }

        player.performCommand("/schematic load " + get_path(args[1], wo_path, false));
    }


    // ------------------------------ Save -----------------------------------------------------------------------------
    private static void command_save(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String wo_path, @NotNull Player player) {
        if (args.length > 3) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
        }
        if (args.length < 2) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
        }

        if (args.length == 2) {
            //If not overwrite
            File file = validate_file(sender, args[1], wo_path, true);
            if (file != null && file.exists()) {
                sender.sendMessage(UiHandler.file_already_exists_error(args[1]));
            } else {
                player.performCommand("/schematic save " + get_path(args[1], wo_path, false));
            }
        } else {
            //If overwrite
            if (!args[2].equals("-f") && !args[2].equals("")) {
                sender.sendMessage(UiHandler.wrong_parameters_error());
                return;
            }
            player.performCommand("/schematic save " + get_path(args[1], wo_path, false) + " -f");
        }
    }

    // ------------------------------ Size -----------------------------------------------------------------------------
    private static void command_size(@Nullable CommandSender sender, String[] args, String wo_path) {
        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
        }
        if (args.length < 2) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
        }

        File file = validate_file(sender, args[1], wo_path, true);
        if (file == null) {
            sender.sendMessage();
            return;
        }
        int size = get_size(file);
        sender.sendMessage(UiHandler.size(args[1], size)[0] + " KiloByte");
    }

    private static int get_size(File file) {
        if (file.isFile()) {
            return (int) FileUtils.sizeOf(file) / 1024;
        } else {
            return (int) FileUtils.sizeOfDirectory(file) / 1024;
        }
    }

    // ------------------------------ Copy -----------------------------------------------------------------------------
    private static void command_copy(CommandSender sender, String[] args,Player player) {
        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
            return;
        }

        String flags = "";
        if (args.length == 2) {
            if (!args[1].startsWith("-")) {
                sender.sendMessage(UiHandler.wrong_parameters_error());
                return;
            }
            flags = args[1];
        }
        player.performCommand("/copy " + flags);
        player.performCommand("/schematic save /temporary/" + player.getName() + "_tmp.schem" + " -f");
    }

    // ------------------------------ Paste ----------------------------------------------------------------------------
    private static void command_paste(CommandSender sender, String[] args, Player player) {
        if (args.length > 2) {
            sender.sendMessage(UiHandler.too_many_parameters_error());
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(UiHandler.too_few_parameters_error());
            return;
        }
        String flags = "";
        if (args.length == 2) {
            if (!args[1].startsWith("-")) {
                sender.sendMessage(UiHandler.wrong_parameters_error());
                return;
            }
            flags = args[1];
        }
        player.performCommand("/schematic load /temporary/" + player.getName() + "_tmp.schem");
        player.performCommand("/paste " + flags);
        player.performCommand("schems remove /temporary/" + player.getName() + "_tmp.schem");
    }

    // ------------------------------ Tab Complete ---------------------------------------------------------------------
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tab_list(sender, args);
    }

    public static List<String> tab_list(@Nullable CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("SchematicSorter")) {
            return null;
        }

        LinkedList<String> sub_command = new LinkedList<>();
        LinkedList<String> path_proposals = new LinkedList<>();
        sub_command.add("help");
        sub_command.add("list");
        sub_command.add("cd");
        sub_command.add("remove");
        sub_command.add("add");
        sub_command.add("move");
        sub_command.add("load");
        sub_command.add("save");
        sub_command.add("size");

        List<String> newList = new ArrayList<>();

        if (args.length < 2) {
            if (args[args.length - 1].equals("")) {
                newList = sub_command;
            } else {
                for (String value : sub_command) {
                    if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        newList.add(value);
                }
            }
        }

        if (args.length >= 2) {

            //Check if player has a working directory
            String wo_path = UserStorage.getCwd(player.getUniqueId());

            String path = args[args.length - 1];

            //Differ if folder or something typed
            if (path.endsWith("/") || path.equals("")) {
                path = get_path(path, wo_path, false);
                //If folder list content of this folder
                File file = validate_file(sender, path, null, true);
                if (file == null) {
                    return null;
                }
                File[] files = file.listFiles();

                for (File x : files) {
                    if (x.isFile()) {
                        path_proposals.add(args[args.length - 1] + x.getName());
                    } else {
                        path_proposals.add(args[args.length - 1] + x.getName() + "/");
                    }
                }

                newList = path_proposals;

            } else {
                path = get_path(path, wo_path, false);
                //If file list content of parent folder
                path = one_folder_back(sender, path);
                File file = validate_file(sender, path, null, true);
                if (file == null) {
                    return null;
                }
                File[] files = file.listFiles();

                if (path == null) {
                    path = "";
                }
                path = path.equals("") ? path : path + "/";

                if (path.equals(wo_path)) {
                    //In case of Working dir
                    for (File x : files) {
                        if (x.isFile()) {
                            path_proposals.add(x.getName());
                        } else {
                            path_proposals.add(x.getName() + "/");
                        }
                    }
                } else {
                    //Standard
                    for (File x : files) {
                        if (x.isFile()) {
                            path_proposals.add(path + x.getName());
                        } else {
                            path_proposals.add(path + x.getName() + "/");
                        }
                    }
                }
                for (String value : path_proposals) {
                    if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        newList.add(value);
                }
            }
        }
        return newList;
    }
}


