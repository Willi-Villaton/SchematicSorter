package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.CPTimer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;

public class SCopy {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList("-[<WE-flags '-be'>]-", "-b", "-e", "-be"));

    //Parameters are [0]copy ([1]flag) ([2]flag)
    private static final int MAX_PARAM = 3;
    private static final int MIN_PARAM = 1;

    public static TextComponent[] command(String[] args, Player player) {

        if (args.length > MAX_PARAM) {
            return UiHandler.too_many_parameters_error();
        }
        if (args.length < MIN_PARAM) {
            return UiHandler.too_few_parameters_error();
        }

        StringBuilder copy_flags = new StringBuilder("//copy ");
        for (int i = 1; i < args.length; i++) {

            switch (args[i]) {
                case "-b":
                    copy_flags.append("-b ");
                    continue;
                case "-e":
                    copy_flags.append("-e ");
                    continue;
                case "-be":
                    copy_flags.append("-be");
                    continue;
                case "-m":
                    if (i + 1 >= args.length) {
                        break;
                    }

                    //This right here is only needed because WorldEdit is incompatible with this combo.
                    if (copy_flags.toString().contains("-be")) {
                        break;
                    }

                    copy_flags.append("-m ").append(args[i + 1]).append(" ");
                    i++;
                    continue;
            }

            return UiHandler.wrong_parameters_error();
        }

        //Issue copy
        player.chat(copy_flags.toString());

        //Issue delayed temporary save
        CPTimer.add_timer(player, "/schems save ./temporary/" + player.getName() + "-temp");
        return null;
    }

    public static LinkedList<String> get_tab_proposals(String[] args) {

        LinkedList<String> tab_list = new LinkedList<>();

        if (args.length >= MIN_PARAM && args.length <= MAX_PARAM) {
            tab_list.addAll(0, FLAGS_AND_INFO);
        }
        return tab_list;
    }
}
