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

# TODO This is an workaround. The API with arguments is not stable.
if [ -z "$1" ]; then
	userFolder=$HOME
else
	userFolder=$1
fi


mkdir -p $userFolder/bin/net.splitcells.os.state.interface.commands.managed
mkdir -p $userFolder/.config/net.splitcells.os.state.interface

# Adds this framework only to interactive shells.
# Therefore only ".bashrc" is currently configured.

# TODO Support alternative shells.

touch $userFolder/.bashrc
grep -q -F '. ~/bin/net.splitcells.os.state.interface.commands.managed/command.managed.export.bin' $userFolder/.bashrc
if [ "$?" -ne "0" ]; then
	echo '. ~/bin/net.splitcells.os.state.interface.commands.managed/command.managed.export.bin' >> $userFolder/.bashrc
	echo '' >> $userFolder/.bashrc
	echo You may need to restart the computer in order to access the installed programs.
	echo At the very least you need to execute "'". ~/.bashrc"'" or open a new terminal in order to access these programs.
fi
chmod +x $userFolder/.bashrc
