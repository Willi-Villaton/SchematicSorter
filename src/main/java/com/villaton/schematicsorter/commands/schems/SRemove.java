package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.LinkedList;

public class SRemove {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList(" [<Flag -y>]-", "-y"));

    //Parameters are [0]remove [1]path [2](Flag)
    private static final int MAX_PARAM = 3;
    private static final int MIN_PARAM = 2;

    public static TextComponent[] command(String[] args, Player player) {

        //Checking for wrong parameters
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

        if (working_path.getPath().equals("")) {
            return UiHandler.unexpected_error(new UnexpectedException("You cannot delete the root-directory!"));
        }

        if (args.length == 3 && args[2].equals("-y")) {

            if (working_path.isFolder()) {
                try {
                    FileUtils.deleteDirectory(working_path.getInstance());
                } catch (IOException e) {
                    return UiHandler.unexpected_error(e);
                }
                return UiHandler.delete();
            } else {
                boolean result = working_path.getInstance().delete();

                if (!result) {
                    return UiHandler.unexpected_error(new UnexpectedException("Delete failed!"));
                } else {
                    return UiHandler.delete();
                }
            }

        } else {
            return UiHandler.remove(working_path.getName(), working_path.getPath());
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
