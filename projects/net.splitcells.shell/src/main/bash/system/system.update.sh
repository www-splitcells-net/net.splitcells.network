#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This command install all available updates.
# If not all update repositories are reachable, a warning is echoed and the program exits successfully.
# In this case "system.state" should also echo, that not all system updates are available.
# If an update requires a system restart this is managed by the appropriate program.
# Therefore, an restart is not required for all system updates.

command.managed.execute conjunction system.update
