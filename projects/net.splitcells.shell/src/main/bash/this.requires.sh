#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Exits the current shell, if a required command is not present.
# If this is the case an error is signaled, because the given task can not be completed.

if [ -x "$(command -v $1)" ]; then
	:; # empty command
else
	exit 1
fi