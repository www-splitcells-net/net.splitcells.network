#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Updates user command repositories.

if [ -z "$NET_SPLITCELLS_SHELL_CONFIG_FOLDER" ]; then
  configFolder="$HOME/.config/net.splitcells.shell"
else
  configFolder="$NET_SPLITCELLS_SHELL_CONFIG_FOLDER"
fi

repoList=$configFolder/command.repositories
hasPrefix() { case $2 in "$1"*) true;; *) false;; esac; }
while IFS= read -r property
do
	if hasPrefix 'repo=' "$property"; then
		echo Updating "'$property'".
		propertyValue=$(echo $property | cut -c6-)
		cd $propertyValue
		repu.pull
	fi
done < "$repoList"
