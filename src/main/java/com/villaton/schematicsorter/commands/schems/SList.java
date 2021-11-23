package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;

public class SList {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList(" [<Flag -a|z|n|o|s|b>] [<Flag -p (0..N)>]-", "-a", "-z", "-n", "-o", "-b", "-s", "-p"));

    //Parameters are [0]list ([1]path/flag) ([2]path/flag) ([3]path/flag) ([4]path/flag)
    private static final int MAX_PARAM = 5;
    private static final int MIN_PARAM = 1;

    public static TextComponent[] command(String[] args, Player player) {

        // 7 is unreachable -> Error state
        int sort = 7;
        int page = 0;
        WorkingPath working_path = null;

        if (args.length > MAX_PARAM) {
            return UiHandler.too_many_parameters_error();
        }

        if (args.length < MIN_PARAM) {
            return UiHandler.too_few_parameters_error();
        }

        for (int i = 1; i < args.length; i++) {

            //Check for path
            WorkingPath new_working_path = WorkingPath.create_path(player, args[i], true, -1);
            if (new_working_path.isValid()) {

                //In case of valid file, but folder
                if (!new_working_path.isFolder()) {
                    return UiHandler.target_is_file_error();
                }

                //In case there isn't already a path in a previous arg
                if (working_path == null) {
                    working_path = new_working_path;
                    continue;
                }
            }

            //Check for sort type
            switch (args[i]) {
                case "-a":
                case "0":
                    sort = 0;
                    continue;
                case "-z":
                case "1":
                    sort = 1;
                    continue;
                case "-s":
                case "2":
                    sort = 2;
                    continue;
                case "-b":
                case "3":
                    sort = 3;
                    continue;
                case "-n":
                case "4":
                    sort = 4;
                    continue;
                case "-o":
                case "5":
                    sort = 5;
                    continue;
            }

            if (i < args.length - 1 && args[i].equals("-p") && args[i + 1].matches("[\\d]+")) {

                try {
                    page = Integer.parseInt(args[i + 1]) - 1;
                } catch (NumberFormatException e) {
                    return UiHandler.wrong_parameters_error();
                }

                //I++ To skip one parameter (Page index)
                i++;
                continue;
            }

            return UiHandler.wrong_parameters_error();
        }

        if (working_path == null) {
            working_path = WorkingPath.create_path(player, "", false);
        }

        //Sorting the given path
        if (sort != 7) {
            WorkingPath.sort_files_in_folder(working_path, sort);
        }

        return UiHandler.list(working_path, page);
    }

    public static LinkedList<String> get_tab_proposals(Player player, String[] args) {

        LinkedList<String> tab_list = new LinkedList<>();

        if (args.length >= MIN_PARAM && args.length <= MAX_PARAM) {
            tab_list = Schems.path_tab_complete(player, args[args.length - 1]);

            if (tab_list.size() != 0 && tab_list.contains("-[<path>]")) {
                tab_list.set(0, tab_list.get(0) + FLAGS_AND_INFO.get(0));
                tab_list.addAll(1, FLAGS_AND_INFO.subList(1, FLAGS_AND_INFO.size()));
            }
        } else {
            tab_list.add(0, "-[<path]" + FLAGS_AND_INFO.get(0));
        }
        return tab_list;
    }
}
