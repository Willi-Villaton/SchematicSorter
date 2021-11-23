package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.io.File;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.LinkedList;

public class SAdd {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList("-", ""));

    //Parameters are [0]add [1]path
    private static final int MAX_PARAM = 2; // +1
    private static final int MIN_PARAM = 2; // -1

    public static TextComponent[] command(String[] args, Player player) {

        if (args.length > MIN_PARAM) {
            return UiHandler.too_many_parameters_error();

        }
        if (args.length < MAX_PARAM) {
            return UiHandler.too_few_parameters_error();
        }

        WorkingPath working_path = WorkingPath.create_path(player, args[1], false);

        //In case there is neither a parent dir nor the given path.
        if (!working_path.isValid() && !working_path.isFolder()) {
            try {
                boolean result = new File(working_path.getPath_with_root()).mkdirs();

                if (!result) {
                    return UiHandler.unexpected_error(new UnexpectedException("Cannot create folder"));
                }
            } catch (Exception e) {
                return UiHandler.unexpected_error(e);
            }
            working_path = WorkingPath.create_path(player, "." + working_path.getPath(), false);
            return UiHandler.add(working_path.getName(), working_path.getPath());
        } else {
            return UiHandler.folder_already_exists_error(working_path.getPath());
        }
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
