package com.villaton.schematicsorter.commands.schems;

import com.villaton.schematicsorter.commands.Schems;
import com.villaton.schematicsorter.ui.UiHandler;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.LinkedList;

public class SHelp {

    public static TextComponent[] command(String[] args) {

        //Preparing result
        TextComponent[] result;

        if (args.length <= 1) {
            //General help menu
            result = UiHandler.help_menu();
        } else {
            if (args.length > 2) {
                result = UiHandler.too_many_parameters_error();
            } else {
                switch (args[1]) {
                    case "help":
                        result = UiHandler.help_submenu_help();
                        break;
                    case "list":
                        result = UiHandler.help_submenu_list();
                        break;
                    case "cd":
                        result = UiHandler.help_submenu_cd();
                        break;
                    case "remove":
                        result = UiHandler.help_submenu_remove();
                        break;
                    case "add":
                        result = UiHandler.help_submenu_add();
                        break;
                    case "move":
                        result = UiHandler.help_submenu_move();
                        break;
                    case "load":
                        result = UiHandler.help_submenu_load();
                        break;
                    case "save":
                        result = UiHandler.help_submenu_save();
                        break;
                    case "size":
                        result = UiHandler.help_submenu_size();
                        break;
                    case "copy":
                        result = UiHandler.help_submenu_copy();
                        break;
                    case "paste":
                        result = UiHandler.help_submenu_paste();
                        break;
                    default:
                        result = UiHandler.wrong_help_command_error();
                        break;
                }
            }
        }
        return result;
    }

    public static LinkedList<String> get_tab_proposals(String[] args) {

        LinkedList<String> tab_list = new LinkedList<>();

        if (args[args.length - 1].equals("")) {
            tab_list = Schems.getSubCommands();
        } else {
            for (String value : Schems.getSubCommands()) {
                if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    tab_list.add(value);
            }
        }
        return tab_list;
    }
}
