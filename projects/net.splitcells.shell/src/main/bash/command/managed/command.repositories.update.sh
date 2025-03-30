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

# Updates user command repositories.

if [ -z "$NET_SPLITCELLS_SHELL_CONFIG_FOLDER" ]; then
  configFolder="$NET_SPLITCELLS_SHELL_CONFIG_FOLDER"
else
  configFolder="$HOME/.config/net.splitcells.shell"
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
