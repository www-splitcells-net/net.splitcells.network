#!/usr/bin/env sh
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
set -e
# Define test.routine configuration.
	baseFolder=$(pwd)
	testContent='h8u9w4f'
# Create repository for testing.
	mkdir a
	cd a
	repo.create
	echo $testContent >> ./text
	repo.commit.all
# Create and test.routine clone.
	cd $baseFolder
	mkdir b
	cd b
	repo.clone.into.current ../a
	if [ "$testContent" != "$(cat ./text)" ]; then
		echo.error '"repo.clone.into.current" does not work.'
		exit 1
	fi
# Clean up all files.
	cd $baseFolder
	rm -rf ./a
	rm -rf ./b