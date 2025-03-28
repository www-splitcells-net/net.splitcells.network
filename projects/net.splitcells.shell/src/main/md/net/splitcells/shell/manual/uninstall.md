----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Uninstall This Software

## Delete All Repositories
Open the text file "~/.config/net.splitcells.shell/command.repositories".
Delete every path that is assigned to repo.

For example:
If you installed this framework in the documents folder of the user's home you will find following entry:
"repo=/home/<username>/Documents/net.splitcells.shell"

## Delete User Configuration

Delete "~/.config/net.splitcells.shell", which contains all user specific configurations.

## Delete Bin Folder

Delete "~/bin/net.splitcells.shell.commands.managed", which contains all commands installed by this framework.

## Remove Bash Configuration

The file "~/.bashrc" contains the terminal configuration for this framework.
It is located under the section "# Export path to net.splitcells.shell scripts.".
Remove this section.

## End

Now you successfully burried the framework!
Rest in peace!
