#!/usr/bin/env sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

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
