#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# TODO Does not work, if the GUI's width of the shell is changed.

shell_width=$(shell.width)
# Clear current line.
	# Sets the cursor to the beginning of the line.
	# Overwrites all symbols in current line, by print as many symbols as the console is wide.
	# Setting the cursor to the beginning of the line, so that next print can write to the start of this line.
	printf "\r$(repeat ' ' times $shell_width)\r"
# TODO Fallback if shell_width could not be determined correctly.
	# IDEA Do not print output expect for warning that filtering does not work
	# correctly. The warning would be printed via echo.filtered.
	# The message should be as small as possible, in order to support small terminals.
if [ $? -eq 0 ]; then
	echo "$@" | cut -c -"$shell_width" | xargs -n 1 -d '\n' echo.filtered
else
	echo.filtered Echo filtering does not work.
fi
