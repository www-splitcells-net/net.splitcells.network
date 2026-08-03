#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Register repository for "command.managed.install.project.commands".

configFolder=~/.config/net.splitcells.shell
mkdir -p $configFolder
repoList=$configFolder/project.repositories
touch $repoList
if grep -q "^$1$" "$repoList"; then
	echo.debug Project repository "'"$1"'" is already registered.
else
	echo "$1" >> $repoList
fi
