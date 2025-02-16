#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

if [ -z "$NET_SPLITCELLS_SHELL_PATH" ]; then
  echo "$HOME/bin/net.splitcells.shell.commands.managed" # Realpath is not used, because it does not work, when the path does not exist.
else
  echo $NET_SPLITCELLS_SHELL_PATH
fi