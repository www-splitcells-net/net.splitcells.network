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

# Executes command in the background that are closed when the current console is closed.
# Should be executed like this: ". shell.execute.as.background.task command_1 & command_2"

echo.error This command is deprecated, because it is not easily portable. This command broke on Ubuntu with version 22.04.1 LTS. Use just normal shell jobs via '&' instead. Leftover programs can be killed with task managers instead.


trap "exit" INT TERM ERR
trap "kill 0" EXIT

$@ &
