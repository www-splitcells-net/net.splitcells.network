#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This scripts needs to be as portable as possible,
# as it is run, before the Shell project is installed the first time.
# Therefore things like `echo.debug` are not available.
if [ -z "$NET_SPLITCELLS_SHELL_CONFIG_FOLDER" ]; then
  configFolder="$HOME/.config/net.splitcells.shell"
else
  configFolder="$NET_SPLITCELLS_SHELL_CONFIG_FOLDER"
fi
mkdir -p $configFolder
repoList=$configFolder/command.repositories
touch $repoList
if grep -q "^repo=$1$" "$repoList"; then
	echo Repository "'"$1"'" is already registered.
else
	echo "repo=$1" >> $repoList
fi
