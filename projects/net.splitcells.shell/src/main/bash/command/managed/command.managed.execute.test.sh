#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
if [[ "$(command.managed.execute disjunction unknown-command 2>&1)" != *'Could not execute "unknown-command".'* ]]; then
	echo.error '"command.managed.execute" executed not present command in disjunction.'
	exit 1
fi
if [[ "$(command.managed.execute conjunction unknown-command 2>&1)" != *'Could not execute "unknown-command".'* ]]; then
	echo.error '"command.managed.execute" executed not present command in conjunction.'
	exit 1
fi
