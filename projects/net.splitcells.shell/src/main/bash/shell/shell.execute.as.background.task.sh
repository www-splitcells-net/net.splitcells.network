#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Executes command in the background that are closed when the current console is closed.
# Should be executed like this: ". shell.execute.as.background.task command_1 & command_2"

echo.error This command is deprecated, because it is not easily portable. This command broke on Ubuntu with version 22.04.1 LTS. Use just normal shell jobs via '&' instead. Leftover programs can be killed with task managers instead.


trap "exit" INT TERM ERR
trap "kill 0" EXIT

$@ &
