#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This command is needed for shells without setup environments.

export HOME=/home/$(whoami)
. "$(command.managed.bin)/command.managed.export.bin"
$@