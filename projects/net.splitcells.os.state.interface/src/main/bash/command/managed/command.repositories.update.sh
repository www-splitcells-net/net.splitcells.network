#!/usr/bin/env sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Updates user command repositories.

repoList=~/.config/net.splitcells.os.state.interface/command.repositories
hasPrefix() { case $2 in "$1"*) true;; *) false;; esac; }
while IFS= read -r property
do
	if hasPrefix 'repo=' "$property"; then
		echo Updating "'$property'".
		propertyValue=$(echo $property | cut -c6-)
		cd $propertyValue
		repu.pull
	fi
done < "$repoList"
