#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# This command install all available updates.
# If not all update repositories are reachable, a warning is echoed and the program exits successfully.
# In this case "system.state" should also echo, that not all system updates are available.
# If an update requires a system restart this is managed by the appropriate program.
# Therefore, an restart is not required for all system updates.

command.managed.execute conjunction system.update
