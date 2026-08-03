#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
folders.current|while read childFolder; do
	cd $childFolder
	echo Processing '"'$(pwd)'"'.
	bash -c "$@"
	cd ..
done
