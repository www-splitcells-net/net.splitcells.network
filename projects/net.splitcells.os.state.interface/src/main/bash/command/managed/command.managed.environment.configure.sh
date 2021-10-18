#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

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
