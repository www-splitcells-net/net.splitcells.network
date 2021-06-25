# Uninstall This Software

## Delete All Repositories
Open the text file "~/.config/net.splitcells.os.state.interface/command.repositories".
Delete every path that is assigned to repo.

For example:
If you installed this framework in the documents folder of the user's home you will find following entry:
"repo=/home/<username>/Documents/net.splitcells.os.state.interface"

## Delete User Configuration

Delete "~/.config/net.splitcells.os.state.interface", which contains all user specific configurations.

## Delete Bin Folder

Delete "~/bin/net.splitcells.os.state.interface.commands.managed", which contains all commands installed by this framework.

## Remove Bash Configuration

The file "~/.bashrc" contains the terminal configuration for this framework.
It is located under the section "# Export path to net.splitcells.os.state.interface scripts.".
Remove this section.

## End

Now you successfully burried the framework!
Rest in peace!

This file is licensed under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
