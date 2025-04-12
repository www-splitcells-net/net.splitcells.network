#!/usr/bin/env bash
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
