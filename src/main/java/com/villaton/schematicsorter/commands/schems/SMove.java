package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.LinkedList;

public class SMove {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList(" (Source) [<path 2>](target)-", ""));

    //Parameters are [0]cd [1]path [2]path
    private static final int MAX_PARAM = 3;
    private static final int MIN_PARAM = 3;

    public static TextComponent[] command(String[] args, Player player) {

        if (args.length > MAX_PARAM) {
            return UiHandler.too_many_parameters_error();
        }
        if (args.length < MIN_PARAM) {
            return UiHandler.too_few_parameters_error();
        }

        WorkingPath working_path = WorkingPath.create_path(player, args[1], true, -1);
        if (!working_path.isValid()) {
            return working_path.getError();
        }

        WorkingPath target_path = WorkingPath.create_path(player, args[2], true, -1);
        if (target_path.isValid() && !target_path.isFolder()) {
            return UiHandler.unable_to_move_error();
        }

        Path source = Paths.get(working_path.getPath_with_root());
        Path target = Paths.get(target_path.getPath_with_root() + "/" + working_path.getName());
        try {
            Files.move(source, target);
        } catch (IOException e) {
            return UiHandler.unexpected_error(new UnexpectedException("Could not move the file. " + e));
        }
        return UiHandler.move(target_path.getPath());
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
