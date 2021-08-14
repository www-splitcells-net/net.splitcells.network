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
	# "user.start" commands should take little time for execution.
	# Make sure that no command blocks indefinitely.
	# i.e. do not just execute "gnome-control-center" as it blocks until the program is closed.
	# Use at least "gnome-control-center & disown". 
user.data.synchronize
	# Ensure that data is up to date as fast as possible.
	# "user.data.synchronize" may implicitly or softly depend on "user.start" (i.e. when it starts a service).
	# The other way around may be also the case,
	# but it is assumed that programs stared by "user.start" can wait for "user.data.synchronize" to happen.
user.configure
user.data.synchronize
	# Do this in case "user.configure" added additional data and data sources.
system.configure
