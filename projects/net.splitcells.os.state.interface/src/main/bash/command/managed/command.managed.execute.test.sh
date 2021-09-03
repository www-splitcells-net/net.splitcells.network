#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT
if [[ "$(command.managed.execute disjunction unknown-command 2>&1)" != *'Could not execute "unknown-command".'* ]]; then
	echo.error '"command.managed.execute" executed not present command in disjunction.'
	exit 1
fi
if [[ "$(command.managed.execute conjunction unknown-command 2>&1)" != *'Could not execute "unknown-command".'* ]]; then
	echo.error '"command.managed.execute" executed not present command in conjunction.'
	exit 1
fi
