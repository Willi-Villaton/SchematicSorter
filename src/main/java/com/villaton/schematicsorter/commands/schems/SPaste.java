package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.ui.UiHandler;
import com.villaton.schematicsorter.utility.CPTimer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;

public class SPaste {

    private static final LinkedList<String> FLAGS_AND_INFO = new LinkedList<>(
            Arrays.asList("-[<WE-flags '-abenos'>]-", "-a", "-b", "-e", "-n", "-o", "-s"));

    //Parameters are [0]paste ([1]flag) ([2]flag) ([3]flag) ([4]flag) ([5]flag) ([6]flag)
    private static final int MAX_PARAM = 7;
    private static final int MIN_PARAM = 1;

    public static TextComponent[] command(String[] args, Player player) {
        if (args.length > MAX_PARAM) {
            return UiHandler.too_many_parameters_error();
        }
        if (args.length < MIN_PARAM) {
            return UiHandler.too_few_parameters_error();
        }

        StringBuilder paste_flags = new StringBuilder("//paste ");
        for (int i = 1; i < args.length; i++) {

            if (args[i].length() == 2) {
                switch (args[i]) {
                    case "-a":
                        paste_flags.append("-a ");
                        continue;
                    case "-o":
                        paste_flags.append("-o ");
                        continue;
                    case "-s":
                        paste_flags.append("-s");
                        continue;
                    case "-n":
                        paste_flags.append("-n");
                        continue;
                    case "-e":
                        paste_flags.append("-e");
                        continue;
                    case "-b":
                        paste_flags.append("-b");
                        continue;
                    case "-m":
                        if (i + 1 >= args.length) {
                            break;
                        }

                        //This right here is only needed because WorldEdit is incompatible with this combo.
                        if (paste_flags.toString().contains("-be")) {
                            break;
                        }

                        paste_flags.append("-m ").append(args[i + 1]).append(" ");
                        i++;
                        continue;
                }
            } else {
                if (args[i].matches("(-(a?)(b?)(e?)(n?)(o?)(s?))") && args.length < 4) {
                    paste_flags.append(args[i]);
                    continue;
                }
            }

            return UiHandler.wrong_parameters_error();
        }

        //Issue paste
        player.chat("/schems load ./temporary/" + player.getName() + "-temp");

        //Issue delayed temporary save
        CPTimer.add_timer(player, paste_flags.toString());
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
