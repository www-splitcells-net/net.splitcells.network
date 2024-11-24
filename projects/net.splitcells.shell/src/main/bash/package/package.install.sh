#!/usr/bin/env bash
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

# Installs a piece of software, in order to provide new commands or functionality.
# The installed object may or may not be active without further actions.
# This command may or may not require extended rights.

# Package names should be a dot separated list of words.
# The Regex pattern package names in this case is "[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*".
# It is preferred, that package names start with a related domain name where each entry
# is present in reverse order.
# If the package has a related URL the path of the URL in the original order
# should be used. 
# This way packages can be organized in an hierarchy and name conflicts can be omitted.

# Use package specific installer, if present, first, in case this framework imports a starter script for this program.
# In this case you cannot check via PATH environment, if the program is already installed.
# One might think, that this is bad, when a program is already installed via other means,
# but otherwise, it is not easily possible to ensure, that everything is present on the computer.
# So, if a piece of software is installed via alternative means,
# it's best to not call `package.install` on it anymore.
# So, if you have a setup script, use the alternative command to install the software or check for its existence,
# instead of using `package.install`.
if [ -x "$(command -v package.install.$1)" ]; then
	echo Executing package.install.$1
	package.install.$1
	if [ "$?" -eq "0" ]; then
		exit
	fi
fi
if [ -x "$(command -v $1)" ]; then
	echo Command \"$1\" found in current path. This means that the package \"$1\" is already installed.
	echo.line.current.clear
	exit
fi
command.managed.execute disjunction package.install $1
