#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

command.managed.execute conjunction user.start
	# Make sure that no command implementation blocks indefinitely,
	# because otherwise not all `user.start` implementations are executed.
	# For example, do not just execute "gnome-control-center" as it blocks until the program is closed.
	# Use at least "gnome-control-center & disown". 
