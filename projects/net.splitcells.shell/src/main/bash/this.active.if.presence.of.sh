#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Exits the current shell, if a required command is not present.
# If this is the case no error is signaled, because in this case it correct for command completion to do nothing.

if [ -x "$(command -v $1)" ]; then
	:; # empty command
else
	exit 0
fi