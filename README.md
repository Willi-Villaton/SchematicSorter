# SchematicSorter
The plug-in has some extended features for managing schematics. In the following, I will simply list all the commands briefly with a sentence of explanation. 
Basically, you can always get more detailed information about parameters etc. via /schems help [optional subcommand].

I will start with the most important basis: the working directory. You can set a working directory within the schematic folder via /schems cd <path>, e.g. x/y/z/.
Then you can perform all Schematic operations directly from this folder and do not always have to specify the path as an argument.
There are a few options for /schems cd

/schems cd <path> -> Set the working directory
/schems cd -s -> Deletes your set directory
/schems cd .. -> Move your working directory one directory up.

And now the important thing: If you have set a directory, paths that you specify operate from this directory.
So for a WD: "x/y/z/" your "/schems list t/s/" becomes internally "/schems list x/y/z/t/s/".
Or, for example, an empty "/schems list" no longer shows you the default folder but "/schems list x/y/z", i.e. the WD.
However, if you want to use the path without the current WD, which also applies to all other commands with paths, then you have to put a dot in front of it. "/schems list ./t/s/"
Then you operate from the default folder as if no WD was set, but without having to switch it off.
This working directory is saved across all servers and also across disconnects and restarts.
Commands:
/schems list <path> -p [page number] -[n | o | a | z]
-n = newest, -o = oldest, -z = alphabetically descending
By default, alphabetical sorting is used.

/schems remove <path> [-y]
Deletes a schema or folder. With -y the security question is suppressed

/schems add <path>
Creates a folder

/schems move <schematic-path> <new-path>
Moves a schematic.

/schems load <path>
Loads a schematic.

/schems save <path>
Saves the current WE clipboard.

/schems copy
Copies the current selection and saves it to a temporary schematic under "/temporary/Playername_tmp.schem".

/schems paste
Loads the temporary schematic file into the clipboard, pastes it and offers to delete it directly.

These are mostly commands that are also in the standard WE or FAWE, but only in this way are they compatible with the WD and tab completion.
If you have any questions or suggestions, please do not hesitate to contact me.
