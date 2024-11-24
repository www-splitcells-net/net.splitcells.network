#!/usr/bin/env sh
# Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the Eclipse
# Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
# which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

if [ -z "$PS1" ]; then
	echo 100
	exit
fi
if [ -x "$(command -v tput)" ]; then
	numberOfColumns=$(tput cols) 2> /dev/null
	if [ $? -eq 0 ]; then
		echo $numberOfColumns
		exit
	fi
fi
if test -f "/dev/pts/1"; then # FIXME Does not work, if multiple displays are present and terminal is moved between these.
	stty -a <"/dev/pts/1" | # Get all terminal settings
	grep -o 'columns [^"]*' | # Get the line containing the number of columns and omit the line's part before the width information.
	cut -c 9- | # Remove the prefix 'columns ', which is right before the number of columns..
	grep -oE '^[0-9]*' # Remove the every character after the number of columns.
	exit
fi
if [ -z "$COLUMNS" ]; then
	echo "$COLUMNS"
	exit
fi
exit 1
