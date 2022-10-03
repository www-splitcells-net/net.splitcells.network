#!/usr/bin/env sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Executes command in the background that are closed when the current console is closed.
# Should be executed like this: ". shell.execute.as.background.task command_1 & command_2"

echo.error This command is deprecated, because it is not easily portable. This command broke on Ubuntu with version 22.04.1 LTS. Use just normal shell jobs via '&' instead. Leftover programs can be killed with task managers instead.


trap "exit" INT TERM ERR
trap "kill 0" EXIT

$@ &
