package com.villaton.schematicsorter.commands;

//Import own classes
import com.villaton.schematicsorter.ui.UiHandler;

//Import subcommand classes
import com.villaton.schematicsorter.commands.schems.SHelp;
import com.villaton.schematicsorter.commands.schems.SList;
import com.villaton.schematicsorter.commands.schems.SCd;
import com.villaton.schematicsorter.commands.schems.SRemove;
import com.villaton.schematicsorter.commands.schems.SAdd;
import com.villaton.schematicsorter.commands.schems.SMove;
import com.villaton.schematicsorter.commands.schems.SLoad;
import com.villaton.schematicsorter.commands.schems.SSave;
import com.villaton.schematicsorter.commands.schems.SSize;
import com.villaton.schematicsorter.commands.schems.SCopy;
import com.villaton.schematicsorter.commands.schems.SPaste;

//Import extern classes
import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Schems implements TabExecutor {

    private static final LinkedList<String> SUB_COMMANDS = new LinkedList<>(
            Arrays.asList("help", "list", "cd", "remove", "move", "add", "load", "save", "copy", "paste", "size"));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        //Check if command is executed from a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(UiHandler.no_player_error());
            return true;
        }

        //Validate player
        Player player = (Player) sender;
        if (!player.hasPermission("SchematicSorter")) {
            output_results(player, UiHandler.insufficient_permission());
            return false;
        }

        TextComponent[] returned_result = null;

        //No parameters given.
        if (args.length <= 0) {
            output_results(player, UiHandler.too_few_parameters_error());
        } else {
            //Looking for different sub commands.
            switch (args[0]) {
                case "help":
                    returned_result = SHelp.command(args);
                    break;
                case "list":
                    returned_result = SList.command(args, player);
                    break;
                case "cd":
                    returned_result = SCd.command(args, player);
                    break;
                case "remove":
                    returned_result = SRemove.command(args, player);
                    break;
                case "add":
                    returned_result = SAdd.command(args, player);
                    break;
                case "move":
                    returned_result = SMove.command(args, player);
                    break;
                case "load":
                    returned_result = SLoad.command(args, player);
                    break;
                case "save":
                    returned_result = SSave.command(args, player);
                    break;
                case "size":
                    returned_result = SSize.command(args, player);
                    break;
                case "copy":
                    returned_result = SCopy.command(args, player);
                    break;
                case "paste":
                    returned_result = SPaste.command(args, player);
                    break;
                default:
                    returned_result = UiHandler.wrong_command_error();
                    break;
            }
        }

        //Finally, plotting the returned result to the player
        output_results(player, returned_result);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("SchematicSorter")) {
            return null;
        }

        LinkedList<String> tab_list = new LinkedList<>();
        if (args.length < 2) {
            if (args[args.length - 1].equals("")) {
                tab_list = SUB_COMMANDS;
            } else {
                for (String value : SUB_COMMANDS) {
                    if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        tab_list.add(value);
                }
            }
        }

        if (args.length < 7) {

            if (args.length >= 2 && SUB_COMMANDS.contains(args[0]) && args[1].equals("")) {
                args = new String[] {args[0], ""};
            }

            switch (args[0]) {
                case "help":
                    tab_list.addAll(SHelp.get_tab_proposals(args));
                    break;
                case "cd":
                    tab_list.addAll(SCd.get_tab_proposals(player, args));
                    break;
                case "list":
                    tab_list.addAll(SList.get_tab_proposals(player, args));
                    break;
                case "remove":
                    tab_list.addAll(SRemove.get_tab_proposals(player, args));
                    break;
                case "add":
                    tab_list.addAll(SAdd.get_tab_proposals(player, args));
                    break;
                case "move":
                    tab_list.addAll(SMove.get_tab_proposals(player, args));
                    break;
                case "load":
                    tab_list.addAll(SLoad.get_tab_proposals(player, args));
                    break;
                case "save":
                    tab_list.addAll(SSave.get_tab_proposals(player, args));
                    break;
                case "size":
                    tab_list.addAll(SSize.get_tab_proposals(player, args));
                    break;
                case "copy":
                    tab_list.addAll(SCopy.get_tab_proposals(args));
                    break;
                case "paste":
                    tab_list.addAll(SPaste.get_tab_proposals(args));
                    break;
            }
        }
        return tab_list;

    }

// ---------------------------------------- Utility Methods ------------------------------------------------------------
    private static void output_results(Player player, TextComponent[] output) {

        if (output == null || output.length <= 0) {
            return;
        }

        for (TextComponent t : output) {
            player.spigot().sendMessage(t);
        }
    }

// ---------------------------------------- TabList Methods ------------------------------------------------------------
    public static LinkedList<String> getSubCommands() {
        return SUB_COMMANDS;
    }

    public static LinkedList<String> path_tab_complete(Player player, String potential_path) {

        LinkedList<String> tab_list = new LinkedList<>();

        if (potential_path.equals("")) {
            tab_list.add("-[<path>]");
        }

        WorkingPath working_path = WorkingPath.create_path(player, potential_path, true, -1);
        if (working_path.isValid()) {
            for (File x : working_path.getContents()) {

                WorkingPath cur_x = WorkingPath.create_path(x);
                if (!cur_x.isValid()) {
                    continue;
                }
                if (cur_x.isFolder()) {
                    tab_list.add(cur_x.getPath() + "/");
                } else {
                    tab_list.add(cur_x.getPath());
                }
            }
        } else {
            working_path = WorkingPath.step_one_folder_up(working_path);

            if (working_path.isValid()) {

                for (File x : working_path.getContents()) {

                    WorkingPath cur_x = WorkingPath.create_path(x);
                    if (cur_x.isValid()) {
                        if (cur_x.isFolder()) {
                            tab_list.add(cur_x.getPath() + "/");
                        } else {
                            tab_list.add(cur_x.getPath());
                        }
                    }
                }
            }
        }
        return tab_list;
    }
}
