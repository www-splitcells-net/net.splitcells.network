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
set -e
# Currently there is only one dependency test and therefore this test type only consists of one file.
mkdir -p ~/.cache/net.splitcells.shell/testing
    rm -rf ~/.cache/net.splitcells.shell/testing/*
    cd ~/.cache/net.splitcells.shell/testing
repo.clone.into.current.test > /dev/null 2>&1 || echo.error '"'repo.clone.into.current'"' has no valid implementation.
command.managed.execute.test
user.ssh.key.generate.test
this.requires test.dependencies.0 && command.managed.execute conjunction test.dependencies
rm -rf ~/.cache/net.splitcells.shell/testing