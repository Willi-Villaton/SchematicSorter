package com.villaton.schematicsorter.ui;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.villaton.schematicsorter.commands.Schems;

import java.util.LinkedList;

public class UiHandler {

    private final static int ELEMENTS_PER_PAGE = 10;

    /*
     * This class lists all User Interfaces from this plugin
     */

    // ----------------------------------- Help --------------------------------------
    public static String[] help_menu() {
        return new String[]{
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------",
                //Pages
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems list" + ChatColor.WHITE + ": "
                        + "Lists all schematics in a folder.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems cd" + ChatColor.WHITE + ": "
                        + "Changes the working directory.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems remove" + ChatColor.WHITE + ": "
                        + "Removes a folder or file.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems add" + ChatColor.WHITE + ": "
                        + "Adds a folder.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems move" + ChatColor.WHITE + ": "
                        + "Moves a folder or file between directories.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems load" + ChatColor.WHITE + ": "
                        + "Loads a schematic file to the clipboard.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems save" + ChatColor.WHITE + ": "
                        + "Saves the clipboard to a schematic file.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems size" + ChatColor.WHITE + ": "
                        + "Returns the size of a schematic folder.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems copy" + ChatColor.WHITE + ": "
                        + "Copies and saves a schematic temporarily.",
                ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems paste" + ChatColor.WHITE + ": "
                        + "Pastes temporarily saved schematic.",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_help() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -help " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Displays all commands and their usage.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems help "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "command" + ChatColor.YELLOW + "]",

                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "command" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Specifies the command for which the help is displayed.",

                ChatColor.GRAY + "Flags: (None)",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_list() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -list " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Displays all schematics and folders inside WorldEdit",
                ChatColor.GRAY + "or specific Folder.",
                ChatColor.GRAY + "Usage:",
                ChatColor.GOLD + "/schems list "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> ["
                        + ChatColor.WHITE + "-p (1...n)" + ChatColor.YELLOW + "] ["
                        + ChatColor.WHITE + "- n | o | a | z " + ChatColor.YELLOW + "]",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for",
                ChatColor.GRAY + "   which the files are displayed.",
                "   " + ChatColor.GRAY + "Without arguments standard path",
                ChatColor.GRAY + "   \"WorldEdit/schematics\" or working directory is used",
                ChatColor.GRAY + "   If there is a path with format ./example/test then even if",
                ChatColor.GRAY + "   a workin directory is set it will be used absolute to standard folder",
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-p (1...n)" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Displays a specific page.",
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-n" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with",
                ChatColor.GRAY + "   newest files to the top.",
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-o" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with",
                ChatColor.GRAY + "   oldest files to the top.",
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-a" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the files alphabetically ascending.",
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-z" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the files alphabetically descending.",
                ChatColor.GOLD + ">>> " + ChatColor.GRAY + "By default, the files are displayed as in the folder.",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_cd() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -cd " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-----------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Changes your persisting working directory.",
                ChatColor.GRAY + "If Directory is set the path argument is no longer needed.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems cd "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> / ["
                        + ChatColor.WHITE + "-s" + ChatColor.YELLOW + "]",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for",
                ChatColor.GRAY + "   which the directory is set.",
                "   " + ChatColor.GRAY + "Without arguments standard path",
                ChatColor.GRAY + "   \"WorldEdit/schematics\" is used",
                "   " + ChatColor.GRAY + "If given a path with a starting dot like" + ChatColor.WHITE + ":",
                "   " + ChatColor.YELLOW + "<" + ChatColor.GOLD + ".(/)example/folder(/)" + ChatColor.YELLOW + ">"
                        + ChatColor.GRAY + "then the new path",
                "   " + ChatColor.GRAY + "will be relative to old working directory.",
                "   " + ChatColor.GOLD + "\"..\" " + ChatColor.GRAY + "instead of path argument",
                "   " + ChatColor.GRAY + "steps one folder back.",
                "   " + ChatColor.GRAY + "Without arguments current working directory will be shown.",
                ChatColor.GRAY + "Flags:",
                // *** Flags ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.WHITE + "-s" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Removes your working directory",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_remove() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -remove " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Removes a file or folder.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems remove "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> ["
                        + ChatColor.WHITE + "-y" + ChatColor.YELLOW + "]",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path for is removed.",
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.WHITE + "-y" + ChatColor.YELLOW + "]" + ChatColor.WHITE + ":"
                        + ChatColor.GRAY + " Removes element without asking for correctness.",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_add() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -add " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Adds new folders.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems add "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is added.",
                ChatColor.GRAY + "Flags: (None)",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_move() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -move " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Moves schematics and folders between specific Folder.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems move "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "file_path" + ChatColor.YELLOW + ">"
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "target_folder_path" + ChatColor.YELLOW + ">",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "file_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is moved.",
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "target_folder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path in which the file is moved.",
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_load() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -load " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Loads schematics to WorldEdit Clipboard.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems load "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is loaded to WorldEdit Clipboard.",
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_save() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -save " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Saves WorldEdit Clipboard to schematic file.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems load "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path to which the schematic is saved.",
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_size() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -size " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Displays the size of a (sub)folder.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems size "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">",
                ChatColor.GRAY + "Arguments: ",
                // *** Arguments ***
                " * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for",
                ChatColor.GRAY + "   which the size is displayed.",
                "   " + ChatColor.GRAY + "Without arguments standard path",
                ChatColor.GRAY + "   \"WorldEdit/schematics\"  or working directory is used",
                ChatColor.GRAY + "Flags: (None)",
                // *** Flags ***
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_copy() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -copy " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Copies and saves a selection to temporary schematic.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems copy "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "Worldedit-Flags" + ChatColor.YELLOW + "]",
                ChatColor.GRAY + "Arguments: (None)",
                // *** Arguments ***
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "Worldedit-Flags" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): See Worldedit help for //copy",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    public static String[] help_submenu_paste() {
        return new String[] {
                //Header
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -paste " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------",
                //Pages (Description, Usage, Arguments, Flags)
                ChatColor.GRAY + "Loades and pastes a temporary schematic.",
                ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems paste "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "Worldedit-Flags" + ChatColor.YELLOW + "]",
                ChatColor.GRAY + "Arguments: (None)",
                // *** Arguments ***
                ChatColor.GRAY + "Flags: ",
                // *** Flags ***
                " * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "Worldedit-Flags" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): See Worldedit help for //paste",
                //Close
                "" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------"
        };
    }

    // ----------------------------------- List --------------------------------------
    public static @NotNull LinkedList<TextComponent[]> list(LinkedList<String>[] elements, int page, @Nullable CommandSender sender, int sort) {

        // elements: 0 = folders, 1 = schematics, 2 = folder_dates, 3 = schematic_dates, 4 = folder_sizes, 5 = schematic_sizes, 6 = path

        int total_elements = elements[1].size() + elements[0].size();
        int total_pages = (int) Math.floor(total_elements / ELEMENTS_PER_PAGE);
        //Wrong page index
        if (page > total_pages) {
            LinkedList<TextComponent[]> error = new LinkedList<>();
            error.add(new TextComponent[] {
                    new TextComponent(wrong_parameters_error()[0])
            });
            return error;
        }

        String[][] page_content = new String[ELEMENTS_PER_PAGE][4];
        if (page * ELEMENTS_PER_PAGE <= elements[1].size()) {
            int pos_on_page = 0;
            for (int pos_in_list = page * ELEMENTS_PER_PAGE; pos_in_list < elements[1].size() && pos_on_page < ELEMENTS_PER_PAGE; pos_in_list++, pos_on_page++) {
                page_content[pos_on_page][0] = elements[1].get(pos_in_list);
                page_content[pos_on_page][1] = "F";
                page_content[pos_on_page][2] = elements[3].get(pos_in_list);
                page_content[pos_on_page][3] = elements[5].get(pos_in_list);
            }
            if (pos_on_page < ELEMENTS_PER_PAGE) {
                for (int pos_in_list = 0; pos_in_list < elements[0].size() && pos_on_page < ELEMENTS_PER_PAGE; pos_in_list++, pos_on_page++) {
                    page_content[pos_on_page][0] = elements[0].get(pos_in_list);
                    page_content[pos_on_page][1] = "S";
                    page_content[pos_on_page][2] = elements[2].get(pos_in_list);
                    page_content[pos_on_page][3] = elements[4].get(pos_in_list);
                }
            }
        } else {
            for (int pos_in_list =  page * ELEMENTS_PER_PAGE - elements[1].size(), pos_on_page = 0; pos_in_list < elements[0].size() && pos_on_page < ELEMENTS_PER_PAGE; pos_in_list++, pos_on_page++) {
                page_content[pos_on_page][0] = elements[0].get(pos_in_list);
                page_content[pos_on_page][1] = "S";
                page_content[pos_on_page][2] = elements[2].get(pos_in_list);
                page_content[pos_on_page][3] = elements[4].get(pos_in_list);
            }
        }

        //Now all page contents should be ready to be displayed
        LinkedList<TextComponent[]> output = new LinkedList<>();

        //Header
        output.add(new TextComponent[] {
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------"
                        + ChatColor.RESET + ChatColor.WHITE + " List " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------")
        });
        TextComponent[] header = new TextComponent[] {
                new TextComponent("")
        };

        //Enable step back
        if (!elements[6].get(0).equals("")) {
            header[0].setText(ChatColor.YELLOW + "<-- " + ChatColor.GOLD + "Back");
            header[0].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Step one folder back").color(ChatColor.GOLD).create()));
            String path = Schems.one_folder_back(sender, elements[6].get(0));
            path = path.startsWith(" ") ? path.substring(1) : path;
            header[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list ." + path));
            output.add(header);
        }

        //Pages
        for (int i = 0; i < page_content.length; i++) {

            TextComponent[] current_element;

            //Close for partially filled page
            if(page_content[i][0] == null) {
                page++;
                total_pages++;
                TextComponent[] last_page = new TextComponent[] {
                        new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH
                                + "-----------------"),
                        new TextComponent(ChatColor.YELLOW + " <<"),
                        new TextComponent("" + ChatColor.RESET + ChatColor.WHITE + " Page "
                                + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + total_pages + " "),
                        new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-------------------")
                };

                page -= 1;
                String sorting = "";
                switch (sort) {
                    case 1:
                        sorting = " -n ";
                        break;
                    case 2:
                        sorting = " -o ";
                        break;
                    case 3:
                        sorting = " -a ";
                        break;
                    case 4:
                        sorting = " -z ";
                        break;
                }
                if (page > 0) {
                    last_page[0].setText("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH
                            + "---------------");
                    last_page[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Show previous page").color(ChatColor.GOLD).create()));
                    last_page[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/schems list ." + Schems.get_path(elements[6].get(0), null,false) + " -p " + page + sorting));
                } else {
                    last_page[1].setText("");
                }
                output.add(last_page);
                return output;
            }
            //End of close for partially filled page

            TextComponent hover;
            //Page content
            if (page_content[i][1].equals("F")) {
                current_element = new TextComponent[] {
                        new TextComponent(ChatColor.GRAY + "? "),
                        new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "] "),
                        new TextComponent(ChatColor.LIGHT_PURPLE + "-F- "),
                        new TextComponent(ChatColor.WHITE + ": "),
                        new TextComponent(ChatColor.DARK_GREEN + page_content[i][0])
                };

                //Hover and Click for remove
                current_element[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to delete this folder.").color(ChatColor.GOLD).create()));
                current_element[1].setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/schems remove ." + Schems.get_path(page_content[i][0], elements[6].get(0),false)));
                //Hover for -F-
                current_element[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("-F- for folder, -S- for schematic.").color(ChatColor.GOLD).create()));
                //Hover and Click for loading
                hover = new TextComponent(new ComponentBuilder("Click to display the content of this folder.").color(ChatColor.GOLD)
                        .append(ComponentSerializer.parse("{text: \"\n\"}"))
                        .append("Size of this folder: " + page_content[i][2] + ", Last Modified: " + page_content[i][3]).color(ChatColor.GOLD).create());
                current_element[4].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(hover).color(ChatColor.GOLD).create()));
                current_element[4].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/schems list ." + Schems.get_path(page_content[i][0], elements[6].get(0),false)));

            } else {
                current_element = new TextComponent[] {
                        new TextComponent(ChatColor.GRAY + "? "),
                        new TextComponent(""), //Weird error if missing. This gets removed by some weird bug.
                        new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "] "),
                        new TextComponent(ChatColor.GOLD + "-S- "),
                        new TextComponent(ChatColor.WHITE + ": "),
                        new TextComponent(ChatColor.DARK_GREEN + page_content[i][0])
                };
                //Hover and Click for remove
                current_element[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to delete this schematic.").color(ChatColor.GOLD).create()));
                current_element[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/schems remove ." + Schems.get_path(page_content[i][0], elements[6].get(0),false) + ".schem"));
                //Hover for -S-
                current_element[3].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("-F- for folder, -S- for schematic.").color(ChatColor.GOLD).create()));
                //Hover and Click for loading
                hover = new TextComponent(new ComponentBuilder("Click to load this schematic.").color(ChatColor.GOLD)
                        .append(ComponentSerializer.parse("{text: \"\n\"}"))
                        .append("Size of this file: " + page_content[i][2] + ", Last Modified: " + page_content[i][3]).color(ChatColor.GOLD).create());
                current_element[5].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(hover).color(ChatColor.GOLD).create()));
                current_element[5].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/schems load ." + Schems.get_path(page_content[i][0], null,false)));
            }
            output.add(current_element);

            //Close for fully filled page
            if (i == page_content.length - 1) {
                page++;
                output.add(new TextComponent[]{
                        new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH
                                + "-----------------"),
                        new TextComponent(ChatColor.YELLOW + " <<"),
                        new TextComponent("" + ChatColor.RESET + ChatColor.WHITE + " Page "
                                + ChatColor.GOLD + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + total_pages + " "),
                        new TextComponent(ChatColor.YELLOW + ">> "),
                        new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------")
                });

                page--;
                String sorting = "";
                switch (sort) {
                    case 1:
                        sorting = " -n ";
                        break;
                    case 2:
                        sorting = " -o ";
                        break;
                    case 3:
                        sorting = " -a ";
                        break;
                    case 4:
                        sorting = " -z ";
                        break;

                }
                if (page > 0) {
                    output.get(output.size() - 1)[0].setText("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH
                            + "---------------");
                    output.get(output.size() - 1)[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Show previous page").color(ChatColor.GOLD).create()));
                    output.get(output.size() - 1)[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/schems list ." + Schems.get_path(elements[6].get(0), null, false) + " -p " + page + sorting));
                } else {
                    output.get(output.size() - 1)[1].setText("");
                }
                page += 2;
                if (page - 1 < total_pages) {
                    output.get(output.size() - 1)[3].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Show next page").color(ChatColor.GOLD).create()));
                    output.get(output.size() - 1)[3].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/schems list ." + Schems.get_path(elements[6].get(0), null,false) + " -p " + page + sorting));
                    output.get(output.size() - 1)[4].setText("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH
                            + "---------------");
                } else {
                    output.get(output.size() - 1)[3].setText("");
                }
            }
            //End of close for fully filled page
        }

        return output;
    }

    // ----------------------------------- Cd --------------------------------------
    public static TextComponent[] cd(String path) {
        TextComponent header = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "New working directory"
                + ChatColor.WHITE + ": ");
        TextComponent cd = new TextComponent(ChatColor.YELLOW + "<" + ChatColor.GOLD + path + ChatColor.YELLOW + ">");
        cd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to show content of folder.").color(ChatColor.GOLD).create()));
        if (!path.equals("Standard folder")) {
            cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list " + path));
        } else {
            cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list"));
        }
        return new TextComponent[] {
                header,
                cd
        };
    }

    public static TextComponent[] cd_display(String path) {
        TextComponent header = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Current working directory"
                + ChatColor.WHITE + ": ");
        TextComponent cd = new TextComponent(ChatColor.YELLOW + "<" + ChatColor.GOLD + path + ChatColor.YELLOW + ">");
        cd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to show content of folder.").color(ChatColor.GOLD).create()));
        if (!path.equals("Standard folder")) {
            cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list " + path));
        } else {
            cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list"));
        }
        return new TextComponent[] {
                header,
                cd
        };
    }

    // ----------------------------------- Remove --------------------------------------
    public static @NotNull TextComponent[] remove(String name) {
        TextComponent header = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                + "Are you sure you want to delete this" + ChatColor.WHITE + ": ");
        TextComponent content = new TextComponent(ChatColor.YELLOW + "<" + ChatColor.GOLD + name + ChatColor.YELLOW + "> ");
        TextComponent close_yes = new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "]");
        close_yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Delete.").color(ChatColor.GOLD).create()));
        close_yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems remove " + name + " -y"));
        return new TextComponent[] {
                header,
                content,
                close_yes
        };
    }

    public static TextComponent[] remove_other_name(String name) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                        + "Did you mean this instead: " + ChatColor.WHITE + ": "),
                new TextComponent(ChatColor.GOLD + name),
                new TextComponent(ChatColor.YELLOW + " [" + ChatColor.RED + "X" + ChatColor.YELLOW + "]")
        };
        output[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Delete.").color(ChatColor.GOLD).create()));
        output[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems remove " + name + " -y"));
        return output;
    }

    public static String[] deleted() {
        return new String[] {
                ChatColor.GOLD + ">> " + ChatColor.WHITE + "Element deleted!"
        };
    }

    // ----------------------------------- Add --------------------------------------
    public static @NotNull TextComponent[] added_folder(String name, String path) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                        + "Folder created: " + ChatColor.WHITE + ": "),
                new TextComponent(ChatColor.GOLD + name)
        };
        output[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Set working directory there.").color(ChatColor.GOLD).create()));
        output[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems cd " + path));
        return output;
    }

    // ----------------------------------- Move --------------------------------------
    public static @NotNull TextComponent[] move_other_name(String name, String target) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                        + "Did you mean this instead: " + ChatColor.WHITE + ": "),
                new TextComponent(ChatColor.GOLD + name),
                new TextComponent(ChatColor.YELLOW + " [" + ChatColor.GREEN + "X" + ChatColor.YELLOW + "]")
        };
        output[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Select.").color(ChatColor.GOLD).create()));
        output[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems move " + name + " " + target));
        return output;
    }

    public static @NotNull TextComponent[] moved_file(String path) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Element moved! - "),
                new TextComponent(ChatColor.YELLOW + "[" + ChatColor.GREEN + "+" + ChatColor.YELLOW + "]")
        };
        output[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Set working directory there.").color(ChatColor.GOLD).create()));
        output[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems cd " + path));
        return output;
    }

    // ----------------------------------- Load --------------------------------------
    public static @NotNull TextComponent[] load_other_name(String name) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                        + "Did you mean this instead: " + ChatColor.WHITE + ": "),
                new TextComponent(ChatColor.GOLD + name),
                new TextComponent(ChatColor.YELLOW + " [" + ChatColor.GREEN + "X" + ChatColor.YELLOW + "]")
        };
        output[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Select.").color(ChatColor.GOLD).create()));
        output[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems load " + name));
        return output;
    }

    // ----------------------------------- Save --------------------------------------
    //Currently not needed

    // ----------------------------------- Size --------------------------------------
    public static @NotNull String[] size(String path, int size) {
        return new String[] {
                ChatColor.GOLD + ">> " + ChatColor.WHITE + "Size of file/folder " + ChatColor.YELLOW + "<" + ChatColor.GOLD
                        + path + ChatColor.YELLOW + ">" + ChatColor.WHITE + ": " + size
        };
    }

    // ----------------------------------- Errors --------------------------------------
    public static @NotNull String[] too_few_parameters_error() {
        return new String[] {ChatColor.RED + "Missing parameters. Try " + ChatColor.BOLD + "/schems help."};
    }

    public static @NotNull String[] too_many_parameters_error() {
        return new String[] {ChatColor.RED + "Too many parameters. Try " + ChatColor.BOLD + "/schems help."};
    }

    public static @NotNull String[] wrong_parameters_error() {
        return new String[] {ChatColor.RED + "Wrong parameters. Try " + ChatColor.BOLD + "/schems help."};
    }

    public static @NotNull String[] invalid_page_error(int total_pages, int page) {
        total_pages++;
        page++;
        return new String[] {
                ChatColor.RED + "Invalid page index. Page: " + page + " of " + total_pages
        };
    }

    public static @NotNull String[] no_player_error() {
        return new String[] {
                ChatColor.RED + "Oh oh oh. This is a player based plug-in. Not working with command prompt."
        };
    }

    public static @NotNull String[] wrong_command_error() {
        return new String[] {
                ChatColor.RED + "Unknown command! See" + ChatColor.BOLD + " /schems help" + ChatColor.RESET,
                ChatColor.RED + "or valid options:",
                ChatColor.GOLD + "/schems <help | list | cd | remove | add | move | load | save | size>"
        };
    }

    public static @NotNull String[] wrong_help_command_error() {
        return new String[] {
                ChatColor.RED + "Invalid parameters! See" + ChatColor.GOLD + " /schems help" + ChatColor.RESET,
                ChatColor.RED + "or valid options:",
                ChatColor.GOLD + "/schems help <help | list | cd | remove | add | size>"
        };
    }

    public static @NotNull String[] invalid_path_error() {
        return new String[] {
                ChatColor.RED + "The given path seems to be incorrect.",
                ChatColor.RED + "Only use sub-directories of " + ChatColor.YELLOW
                        + "<" + ChatColor.GOLD + "/WorldEdit/schematics/" + ChatColor.YELLOW + ">" + ChatColor.WHITE + ".",
                ChatColor.RED + "If your working directory is set you need to",
                ChatColor.RED + "use \"" + ChatColor.YELLOW + "." + ChatColor.RED + "/path\"."
        };
    }

    public static @NotNull TextComponent[] folder_already_exists_error(String path) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.RED + "This folder already exists."),
                new TextComponent("" + ChatColor.RED + ChatColor.BOLD + " Show folder.")
        };
        output[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Lists the content of this folder.").color(ChatColor.GOLD).create()));
        output[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems list " + path));
        return output;
    }

    public static @NotNull TextComponent[] file_already_exists_error(String path) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.RED + "This file " + ChatColor.YELLOW + "<" + ChatColor.GOLD + path
                        + ChatColor.YELLOW + ">" + ChatColor.RED + " already exists."),
                new TextComponent(ChatColor.RED + "Overwrite it? "),
                new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "]")
        };
        output[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Confirm overwrite.").color(ChatColor.GOLD).create()));
        output[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems save " + path + " -f"));
        return output;
    }

    public static @NotNull String[] exit_standard_folder_error() {
        return new String[] {
                ChatColor.RED + "You are already in the standard folder.",
                ChatColor.RED + "External folders are not supported yet."
        };
    }

    public static @NotNull String[] unexpected_error(int error_code) {
        return new String[] {
                ChatColor.RED + "Oops i did it again. Unexpected Error. Errorcode: " + ChatColor.GOLD + error_code
        };
    }

    public static @NotNull String[] insufficient_permission() {
        return new String[] {
                ChatColor.RED + "Insufficient authorisation level.",
                ChatColor.RED + "You are not entitled to perform this command!"
        };
    }

    // ----------------------------------- Getter --------------------------------------
    public static int getElementsPerPage() {
        return ELEMENTS_PER_PAGE;
    }
}
