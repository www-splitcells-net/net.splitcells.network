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

# TODO Document supported repository file structure.
# Installs user commands and registers the installed commands to the PATH variable for new shells.

mkdir -p "$(command.managed.bin)"
find "$(command.managed.bin)" -maxdepth 1 -type f -delete

if [ -z "$NET_SPLITCELLS_SHELL_CONFIG_FOLDER" ]; then
  configFolder="$NET_SPLITCELLS_SHELL_CONFIG_FOLDER"
else
  configFolder="$HOME/.config/net.splitcells.shell"
fi

repoList=$configFolder/command.repositories
hasPrefix() { case $2 in "$1"*) true;; *) false;; esac; }
bootstrapRepoProperty=$(head -n 1 $repoList)
if hasPrefix 'repo=' "$bootstrapRepoProperty"; then
	bootstrapRepo=$(echo $bootstrapRepoProperty | cut -c6-)
		cd $bootstrapRepo
	setupEnvironment=$bootstrapRepo/src/main/bash/command/managed/command.managed.environment.configure.sh
		chmod +x $setupEnvironment
		$setupEnvironment
	chmod +x $bootstrapRepo/src/main/bash/echo/*
		PATH=$bootstrapRepo/src/main/bash/echo:$PATH
	chmod +x $bootstrapRepo/src/main/python/*
		PATH=$bootstrapRepo/src/main/python:$PATH
	chmod +x $bootstrapRepo/src/main/bash/shell/*
		PATH=$bootstrapRepo/src/main/bash/shell:$PATH
	export PATH
	installer=$bootstrapRepo/src/main/python/command/managed/command.managed.install.py
		chmod +x $installer
		$installer $installer
	chmod +x "$(command.managed.bin)/"*
fi
while IFS= read -r property
do
	echo Installing "'$property'".
	if hasPrefix 'repo=' "$property"; then
		propertyValue=$(echo $property | cut -c6-)
		cd $propertyValue
		cd src/main
		if [ -d "sh" ]; then
			cd sh
			find . -type f | sort -n | xargs -r -n 1 $installer
			cd ..
		fi
		if [ -d "bash" ]; then
			cd bash
			find . -type f | sort -n | xargs -r -n 1 $installer
			cd ..
		fi
		if [ -d "python" ]; then
			cd python
			find . -type f | sort -n | xargs -r -n 1 $installer
			cd ..
		fi
		chmod +x "$(command.managed.bin)/"*
		if [ -d ../doc/man1 ]; then
			cd ../doc/man1
			mkdir -p ~/bin/man/man1
			find . -type f | sort -n | xargs -I % cp % ~/bin/man/man1
		fi
	fi
done < "$repoList"
if test -d "$configFolder/src"; then
	cd "$configFolder/src"
	find . -mindepth 1 -type f -exec command.managed.install {} \;
fi
