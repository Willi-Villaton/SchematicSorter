package com.villaton.schematicsorter.ui;

import com.villaton.schematicsorter.utility.WorkingPath;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class UiHandler {

    // --- Private Variables ---
    private final static int ELEMENTS_PER_PAGE = 10;

    /*
     * This class lists all User-Interfaces from this plugin.
     */
// ---------------------------------------- Change Directory -----------------------------------------------------------
    public static TextComponent[] cd(String path) {

        //In case of standard folder
        String output;
        if (path.equals("")) {
            output = "Not set - Standard folder";
        } else {
            output = path;
        }

        //Components
        TextComponent header = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Current working directory"
                + ChatColor.WHITE + ": ");
        TextComponent cd = new TextComponent(ChatColor.YELLOW + "<" + ChatColor.GOLD + output + ChatColor.YELLOW + ">");

        //Hover and ClickEvents
        cd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to show content of folder.").color(ChatColor.GOLD).create()));

        cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list " + path));
        cd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list"));


        //Preparing for return
        return new TextComponent[] {
                header,
                cd
        };
    }

// ---------------------------------------- List Directory -------------------------------------------------------------
    public static TextComponent[] list(WorkingPath working_path, int page) {

        //Calculate pages
        int total_elements = working_path.getContents().length;
        int total_pages = ((int) Math.floor((double)total_elements / ELEMENTS_PER_PAGE)) + 1;

        if (page < 0 || page > total_pages - 1) {
            return UiHandler.invalid_page_index_error(page + 1, total_pages);
        }

        //Calculate elements on page
        int current_page_start = page * ELEMENTS_PER_PAGE;
        int current_page_end = Math.min(current_page_start + ELEMENTS_PER_PAGE, total_elements);

        //IF CODE IN LIST STRUGGLES --- OLD/DEAD CODE
//        if (current_page_start + ELEMENTS_PER_PAGE >= total_elements) {
//            current_page_end = total_elements;
//        } else {
//            current_page_end = current_page_start + ELEMENTS_PER_PAGE;
//        }

        //This is going to be the output. Plus 2 because of header and close
        int header_offset = 3;
        int closer_offset = 1;
        TextComponent[] output = new TextComponent[ELEMENTS_PER_PAGE + header_offset + closer_offset];

        //Generate Header
        output[0] = new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-------------------"
                + ChatColor.RESET + ChatColor.WHITE + " List "
                + ChatColor.RESET + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-------------------");
        output[1] = new TextComponent(ChatColor.YELLOW + "#" + ChatColor.WHITE
                + " Contents of: " + ChatColor.GOLD + working_path.getPath());
        TextComponent step_up = new TextComponent(ChatColor.YELLOW + "<-- " + ChatColor.GOLD + "Back");
        step_up.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Step one folder back").color(ChatColor.GOLD).create()));
        step_up.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/schems list ." + WorkingPath.step_one_folder_up(working_path).getPath()));
        output[2] = step_up;

        //Getting elements on page
        for (int pos_on_page = header_offset; pos_on_page < (current_page_end - current_page_start) + header_offset; pos_on_page++) {

            WorkingPath current_element = WorkingPath.create_path(working_path.getContents()[current_page_start + pos_on_page - header_offset]);
            if (!current_element.isValid()) {
                return current_element.getError();
            }

            //Generate final element
            TextComponent element = new TextComponent();

            //Generate all extras
            TextComponent marker = new TextComponent(ChatColor.GRAY + "? ");
            TextComponent delete = new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "] ");
            TextComponent type;
            TextComponent spacer = new TextComponent(ChatColor.WHITE + ": ");
            TextComponent content = new TextComponent(ChatColor.DARK_AQUA + current_element.getName());

            //Hover and Click Events for every extra
            // --- delete ---
            delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Click to delete this folder.").color(ChatColor.GOLD).create()));
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems remove ." + current_element.getPath()));

            // --- type ---
            if (current_element.isFolder()) {
                type = new TextComponent(ChatColor.LIGHT_PURPLE + "-F- ");
            } else {
                type = new TextComponent(ChatColor.GOLD + "-S- ");
            }

            type.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("-F- for folder, -S- for schematic.").color(ChatColor.GOLD).create()));

            // --- content ---
            if (current_element.isFolder()) {
                //IF FOLDER
                TextComponent hover = new TextComponent(new ComponentBuilder("Click to display the content of this folder.").color(ChatColor.GOLD)
                        .append(ComponentSerializer.parse("{text: \"\n\"}"))
                        .append("Size of this folder: " + WorkingPath.get_formatted_size(current_element.getInstance())
                                + ", Last Modified: " + WorkingPath.get_formatted_date(current_element.getInstance()))
                        .color(ChatColor.GOLD).create());

                content.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(hover).color(ChatColor.GOLD).create()));
                content.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/schems list ." + current_element.getPath()));
            } else {
                //IF FILE
                TextComponent hover = new TextComponent(new ComponentBuilder("Click to load this schematic.").color(ChatColor.GOLD)
                        .append(ComponentSerializer.parse("{text: \"\n\"}"))
                        .append("Size of this file: " + WorkingPath.get_formatted_size(current_element.getInstance())
                                + ", Last Modified: " + WorkingPath.get_formatted_date(current_element.getInstance()))
                        .color(ChatColor.GOLD).create());

                content.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(hover).color(ChatColor.GOLD).create()));
                content.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/schems load ." + current_element.getPath()));
            }

            //Adding extras to base element
            element.addExtra(marker);
            element.addExtra(delete);
            element.addExtra(type);
            element.addExtra(spacer);
            element.addExtra(content);

            //Adding base element into output
            output[pos_on_page] = element;
        }

        //Generate Closer
        TextComponent closer = new TextComponent();

        TextComponent s1 = new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------");
        TextComponent back = new TextComponent(ChatColor.GOLD + " <<");
        TextComponent spacer = new TextComponent(ChatColor.WHITE + " Page " + (page + 1) + ChatColor.YELLOW
                + "/" + ChatColor.WHITE + total_pages + " ");
        TextComponent forward = new TextComponent(ChatColor.GOLD + ">> ");
        TextComponent s2 = new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------");

        // --- Back Button ---
        if (page <= 0) {
            back.setText("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---");
        } else {
            back.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Show previous page").color(ChatColor.GOLD).create()));
            back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list ." + working_path.getPath() + " -p " + (page) + " " + working_path.getSort()));
        }

        // --- Forward button ---
        if (current_page_end - current_page_start < ELEMENTS_PER_PAGE) {
            forward.setText("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---");
        } else {
            forward.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Show next page").color(ChatColor.GOLD).create()));
            forward.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/schems list ." + working_path.getPath() + " -p " + (page + 2) + " " + working_path.getSort()));
        }

        closer.addExtra(s1);
        closer.addExtra(back);
        closer.addExtra(spacer);
        closer.addExtra(forward);
        closer.addExtra(s2);

        //Adding closer into output
        output[ELEMENTS_PER_PAGE + header_offset + closer_offset - 1] = closer;

        return output;
    }

// ---------------------------------------- Add Directory --------------------------------------------------------------
    public static @NotNull TextComponent[] add(String name, String path) {
        TextComponent[] output = new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                        + "Folder created" + ChatColor.WHITE + ": "),
                new TextComponent(ChatColor.GOLD + name)
        };
        output[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Set working directory there.").color(ChatColor.GOLD).create()));
        output[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems cd ." + path));
        return output;
    }

// ---------------------------------------- Remove Directory -----------------------------------------------------------
    public static @NotNull TextComponent[] remove(String name, String path) {

        TextComponent remove = new TextComponent();

        TextComponent header = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE
                + "Are you sure you want to delete this" + ChatColor.WHITE + ": ");
        TextComponent content = new TextComponent(ChatColor.YELLOW + "<" + ChatColor.GOLD + name + ChatColor.YELLOW + "> ");
        TextComponent confirm = new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "]");

        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Delete.").color(ChatColor.GOLD).create()));
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems remove ." + path + " -y"));

        remove.addExtra(content);
        remove.addExtra(confirm);

        return new TextComponent[] {
                header,
                remove
        };
    }

    public static TextComponent[] delete() {
        return new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Element deleted!")
        };
    }

// ---------------------------------------- Move Directory -------------------------------------------------------------
    public static @NotNull TextComponent[] move(String path) {

        TextComponent message = new TextComponent();

        TextComponent text = new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Element moved! - ");
        TextComponent button = new TextComponent(ChatColor.YELLOW + "[" + ChatColor.GREEN + "+" + ChatColor.YELLOW + "]");

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Show directory.").color(ChatColor.GOLD).create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems list " + path + " -n"));

        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Set working directory there.").color(ChatColor.GOLD).create()));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems cd " + path));

        message.addExtra(text);
        message.addExtra(button);

        return new TextComponent[] {
                message
        };
    }

// ---------------------------------------- Get Size -------------------------------------------------------------------
    public static @NotNull TextComponent[] size(String path, String size) {
        return new TextComponent[] {
                new TextComponent(ChatColor.GOLD + ">> " + ChatColor.WHITE + "Size of file/folder " + ChatColor.YELLOW + "<" + ChatColor.GOLD
                        + path + ChatColor.YELLOW + ">" + ChatColor.WHITE + ": " + size)
        };
    }

// ---------------------------------------- Help Menu and Submenus -----------------------------------------------------
    public static TextComponent[] help_menu() {
    return new TextComponent[]{
            //Header
            new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------"
                    + ChatColor.RESET + ChatColor.WHITE + " Help " + ChatColor.RESET
                    + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "--------------------"),
            //Pages
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems list" + ChatColor.WHITE + ": "
                    + "Lists all schematics in a folder."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems cd" + ChatColor.WHITE + ": "
                    + "Changes the working directory."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems remove" + ChatColor.WHITE + ": "
                    + "Removes a folder or file."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems add" + ChatColor.WHITE + ": "
                    + "Adds a folder."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems move" + ChatColor.WHITE + ": "
                    + "Moves a folder or file between directories."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems load" + ChatColor.WHITE + ": "
                    + "Loads a schematic file to the clipboard."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems save" + ChatColor.WHITE + ": "
                    + "Saves the clipboard to a schematic file."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems size" + ChatColor.WHITE + ": "
                    + "Returns the size of a schematic folder."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems copy" + ChatColor.WHITE + ": "
                    + "Copies and saves a schematic temporarily."),
            new TextComponent(ChatColor.GRAY + "? " + ChatColor.GOLD + "/schems paste" + ChatColor.WHITE + ": "
                    + "Pastes temporarily saved schematic."),
            //Close
            new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
    };
}

    public static TextComponent[] help_submenu_help() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -help " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Displays all commands and their usage."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems help "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "command" + ChatColor.YELLOW + "]"),

                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "command" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Specifies the command for which the help is displayed."),

                new TextComponent(ChatColor.GRAY + "Flags: (None)"),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_list() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -list " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Displays all schematics and folders inside WorldEdit"),
                new TextComponent(ChatColor.GRAY + "or specific Folder."),
                new TextComponent(ChatColor.GRAY + "Usage:"),
                new TextComponent(ChatColor.GOLD + "/schems list "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> ["
                        + ChatColor.WHITE + "-p (1...n)" + ChatColor.YELLOW + "] ["
                        + ChatColor.WHITE + "- n | o | b | s | a | z " + ChatColor.YELLOW + "]"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for"),
                new TextComponent(ChatColor.GRAY + "   which the files are displayed."),
                new TextComponent("   " + ChatColor.GRAY + "Without arguments standard path"),
                new TextComponent(ChatColor.GRAY + "   \"WorldEdit/schematics\" or working directory is used"),
                new TextComponent(ChatColor.GRAY + "   If there is a path with format ./example/test then even if"),
                new TextComponent(ChatColor.GRAY + "   a working directory is set it will be used absolute to standard folder"),
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-p (1...n)" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Displays a specific page."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-n" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with"),
                new TextComponent(ChatColor.GRAY + "   newest files to the top."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-o" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with"),
                new TextComponent(ChatColor.GRAY + "   oldest files to the top."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-b" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with"),
                new TextComponent(ChatColor.GRAY + "   biggest files to the top."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-s" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the displayed folder with"),
                new TextComponent(ChatColor.GRAY + "   smallest files to the top."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-a" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the files alphabetically ascending."),
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "-z" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Sorts the files alphabetically descending."),
                new TextComponent(ChatColor.GOLD + ">>> " + ChatColor.GRAY + "By default, the files are displayed alphabetically."),
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_cd() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -cd " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-----------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Changes your persisting working directory."),
                new TextComponent(ChatColor.GRAY + "If Directory is set the path argument is no longer needed."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems cd "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> / ["
                        + ChatColor.WHITE + "-s" + ChatColor.YELLOW + "]"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for"),
                new TextComponent(ChatColor.GRAY + "   which the directory is set."),
                new TextComponent("   " + ChatColor.GRAY + "Without arguments standard path"),
                new TextComponent(ChatColor.GRAY + "   \"WorldEdit/schematics\" is used"),
                new TextComponent("   " + ChatColor.GRAY + "If given a path with a starting dot like" + ChatColor.WHITE + ":"),
                new TextComponent("   " + ChatColor.YELLOW + "<" + ChatColor.GOLD + ".(/)example/folder(/)" + ChatColor.YELLOW + ">"
                        + ChatColor.GRAY + "then the new path"),
                new TextComponent("   " + ChatColor.GRAY + "will be relative to old working directory."),
                new TextComponent("   " + ChatColor.GOLD + "\"..\" " + ChatColor.GRAY + "instead of path argument"),
                new TextComponent("   " + ChatColor.GRAY + "steps one folder back."),
                new TextComponent("   " + ChatColor.GRAY + "Without arguments current working directory will be shown."),
                new TextComponent(ChatColor.GRAY + "Flags:"),
                // *** Flags ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.WHITE + "-s" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): Removes your working directory"),
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_remove() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -remove " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "-------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Removes a file or folder."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems remove "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + "> ["
                        + ChatColor.WHITE + "-y" + ChatColor.YELLOW + "]"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path for is removed."),
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.WHITE + "-y" + ChatColor.YELLOW + "]" + ChatColor.WHITE + ":"
                        + ChatColor.GRAY + " Removes element without asking for correctness."),
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_add() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -add " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Adds new folders."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems add "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is added."),
                new TextComponent(ChatColor.GRAY + "Flags: (None)"),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_move() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -move " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Moves schematics and folders between specific Folder."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems move "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "file_path" + ChatColor.YELLOW + ">"
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "target_folder_path" + ChatColor.YELLOW + ">"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "file_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is moved."),
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "target_folder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path in which the file is moved."),
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_load() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -load " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Loads schematics to WorldEdit Clipboard."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems load "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path which is loaded to WorldEdit Clipboard."),
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_save() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -save " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Saves WorldEdit Clipboard to schematic file."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems load "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "schematic_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + ": Specifies the path to which the schematic is saved."),
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_size() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -size " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Displays the size of a (sub)folder."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems size "
                        + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">"),
                new TextComponent(ChatColor.GRAY + "Arguments: "),
                // *** Arguments ***
                new TextComponent(" * " + ChatColor.YELLOW + "<" + ChatColor.GOLD + "subfolder_path" + ChatColor.YELLOW + ">" + ChatColor.GRAY
                        + " (optional): Specifies the path for"),
                new TextComponent(ChatColor.GRAY + "   which the size is displayed."),
                new TextComponent("   " + ChatColor.GRAY + "Without arguments standard path"),
                new TextComponent(ChatColor.GRAY + "   \"WorldEdit/schematics\"  or working directory is used"),
                new TextComponent(ChatColor.GRAY + "Flags: (None)"),
                // *** Flags ***
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_copy() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -copy " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Copies and saves a selection to temporary schematic."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems copy "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "WorldEdit-Flags" + ChatColor.YELLOW + "]"),
                new TextComponent(ChatColor.GRAY + "Arguments: (None)"),
                // *** Arguments ***
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "WorldEdit-Flags" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): See WorldEdit help for //copy"),
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

    public static TextComponent[] help_submenu_paste() {
        return new TextComponent[] {
                //Header
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------"
                        + ChatColor.RESET + ChatColor.WHITE + " Help for -paste " + ChatColor.RESET
                        + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "---------------"),
                //Pages (Description, Usage, Arguments, Flags)
                new TextComponent(ChatColor.GRAY + "Loads and pastes a temporary schematic."),
                new TextComponent(ChatColor.GRAY + "Usage: " + ChatColor.GOLD + "/schems paste "
                        + ChatColor.YELLOW + "[" + ChatColor.GOLD + "WorldEdit-Flags" + ChatColor.YELLOW + "]"),
                new TextComponent(ChatColor.GRAY + "Arguments: (None)"),
                // *** Arguments ***
                new TextComponent(ChatColor.GRAY + "Flags: "),
                // *** Flags ***
                new TextComponent(" * " + ChatColor.YELLOW + "[" + ChatColor.GOLD + "WorldEdit-Flags" + ChatColor.YELLOW + "]" + ChatColor.GRAY
                        + " (optional): See WorldEdit help for //paste"),
                //Close
                new TextComponent("" + ChatColor.YELLOW + ChatColor.STRIKETHROUGH + "----------------------------------------------")
        };
    }

//----------------------------------------- Error Outputs --------------------------------------------------------------
    // --- Player errors ---
    public static @NotNull TextComponent[] no_player_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Oh oh oh. This is a player based plug-in. Not working with command prompt.")
        };
    }

    public static @NotNull TextComponent[] insufficient_permission() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Insufficient authorisation level."),
                new TextComponent(ChatColor.RED + "You are not entitled to perform this command!")
        };
    }

    // --- Parameter errors ---
    public static @NotNull TextComponent[] too_few_parameters_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Missing parameters. Try " + ChatColor.BOLD + "/schems help.")
        };
    }

    public static @NotNull TextComponent[] too_many_parameters_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Too many parameters. Try " + ChatColor.BOLD + "/schems help.")
        };
    }

    public static @NotNull TextComponent[] wrong_parameters_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Wrong parameters. Try " + ChatColor.BOLD + "/schems help.")
        };
    }

    public static @NotNull TextComponent[] invalid_page_index_error(int page, int total_pages) {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Invalid page " + ChatColor.GOLD + page
                        +ChatColor.YELLOW + "/" + ChatColor.GOLD + total_pages + " index!"),
                new TextComponent("Try " + ChatColor.BOLD + "/schems help.")
        };
    }

    // --- Path errors ---
    public static @NotNull TextComponent[] empty_path_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Fatal: The given path turns out to be null!")
        };
    }

    public static @NotNull TextComponent[] target_is_file_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "The given path is not a folder!"),
                new TextComponent(ChatColor.RED + " -> Therefore it cannot be processed!")
        };
    }

    public static @NotNull TextComponent[] target_is_folder_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "The given path is not a file!"),
                new TextComponent(ChatColor.RED + " -> Therefore it cannot be processed!")
        };
    }

    public static @NotNull TextComponent[] invalid_path_error(String path) {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "The given path " + ChatColor.YELLOW + "(" + ChatColor.GOLD + path
                        + ChatColor.YELLOW + ")" + ChatColor.RED + " seems to be incorrect."),
                new TextComponent(ChatColor.RED + "Only use sub-directories of " + ChatColor.YELLOW
                        + "<" + ChatColor.GOLD + "/WorldEdit/schematics/" + ChatColor.YELLOW + ">" + ChatColor.WHITE + "."),
                new TextComponent(ChatColor.RED + "If your working directory is set you need to"),
                new TextComponent(ChatColor.RED + "use \"" + ChatColor.YELLOW + "." + ChatColor.RED + "/path\".")
        };
    }

    public static @NotNull TextComponent[] root_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Already in root-directory."),
                new TextComponent(ChatColor.RED + "Only use sub-directories of " + ChatColor.YELLOW
                        + "<" + ChatColor.GOLD + "/WorldEdit/schematics/" + ChatColor.YELLOW + ">" + ChatColor.WHITE + "."),
                new TextComponent(ChatColor.RED + "If your working directory is set you need to"),
                new TextComponent(ChatColor.RED + "use \"" + ChatColor.YELLOW + "." + ChatColor.RED + "/path\".")
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

        TextComponent confirmation = new TextComponent();
        TextComponent text = new TextComponent(ChatColor.RED + "Overwrite it? ");
        TextComponent button = new TextComponent(ChatColor.YELLOW + "[" + ChatColor.RED + "X" + ChatColor.YELLOW + "]");

        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Confirm overwrite.").color(ChatColor.GOLD).create()));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/schems save " + path + " -f"));

        confirmation.addExtra(text);
        confirmation.addExtra(button);

        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "This file " + ChatColor.YELLOW + "<" + ChatColor.GOLD + path
                        + ChatColor.YELLOW + ">" + ChatColor.RED + " already exists."),
                confirmation
        };
    }

    public static @NotNull TextComponent[] unable_to_move_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "The given path exists already or something went wrong!"),
                new TextComponent(ChatColor.RED + " -> Therefore file could not be moved!")
        };
    }

    // --- Command errors ---
    public static @NotNull TextComponent[] wrong_command_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Unknown command! See" + ChatColor.BOLD + " /schems help" + ChatColor.RESET),
                new TextComponent(ChatColor.RED + "or valid options:"),
                new TextComponent(ChatColor.GOLD + "/schems <help | list | cd | remove | add | move | load | save | size>")
        };
    }

    public static @NotNull TextComponent[] wrong_help_command_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Invalid parameters! See" + ChatColor.GOLD + " /schems help" + ChatColor.RESET),
                new TextComponent(ChatColor.RED + "or valid options:"),
                new TextComponent(ChatColor.GOLD + "/schems help <help | list | cd | remove | add | move | load | save | size>")
        };
    }

    // --- Technical errors ---
    public static @NotNull TextComponent[] sorting_error() {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Fatal: Something went wrong whilst sorting files!")
        };
    }

    public static @NotNull TextComponent[] unexpected_error(Exception e) {
        return new TextComponent[] {
                new TextComponent(ChatColor.RED + "Fatal: Something critical went wrong!"),
                new TextComponent(ChatColor.GOLD + "Caused by: " + ChatColor.WHITE + e.getMessage())
        };
    }
}
