#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This command need to be able to work without any Shell project installation being present,
# for things like reinstallations.
# In other words, this command is used for bootstrapping a Shell project installation.

set -e
# ~/bin/* is used for binaries, that are placed by the user there manually.
case "$PATH" in
  *":$HOME/bin:"* ) export PATH="$HOME/bin:$PATH" ;;
esac
if [ -z "$NET_SPLITCELLS_SHELL_PATH" ]; then
  export PATH="$HOME/bin/net.splitcells.shell.commands.managed:$PATH"
else
  export PATH="$NET_SPLITCELLS_SHELL_PATH:$PATH"
fi
