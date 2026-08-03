#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects


mkdir -p "$(command.managed.bin)"
mkdir -p ~/.config/net.splitcells.shell

# Adds this framework's PATHs only to interactive shells.
# Therefore only ".bashrc" is currently configured.
# It is best practice to add PATH exports only in interactive shells via `if [[ $- == *i* ]] ; then [...] fi`: https://unix.stackexchange.com/questions/257571/why-does-bashrc-check-whether-the-current-shell-is-interactive

# Placing the exports in `.profile` could cause the framework's PATHs to be provided to shells,
# that are not opened by users directly and therefore may cause problems for other programs.
# The same applies for `bash_profile`.

# TODO Support fish terminal via ` ~/.config/fish/config.fish`: check presence of option via heuristics.
# TODO Add dependency injection for `command.managed.export.bin` in order to easily support additional exports,
# but make it so, that dependency injection is optional and therefore this script also works without `command.managed.export`.

touch ~/.bashrc
grep -q -F ". $(command.managed.bin)/command.managed.export.bin" ~/.bashrc
if [ "$?" -ne "0" ]; then
	echo ". $(command.managed.bin)/command.managed.export.bin" >> ~/.bashrc
	echo '' >> ~/.bashrc
	echo You may need to restart the computer in order to access the installed programs.
	echo At the very least you need to execute "'". ~/.bashrc"'" or open a new terminal in order to access these programs.
fi
chmod +x ~/.bashrc
