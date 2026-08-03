#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

command.managed.execute conjunction user.start
	# Make sure that no command implementation blocks indefinitely,
	# because otherwise not all `user.start` implementations are executed.
	# For example, do not just execute "gnome-control-center" as it blocks until the program is closed.
	# Use at least "gnome-control-center & disown". 
