#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT
set -e
# Currently there is only one dependency test and therefore this test type only consists of one file.
mkdir -p ~/.cache/net.splitcells.os.state.interface/testing
    rm -rf ~/.cache/net.splitcells.os.state.interface/testing/*
    cd ~/.cache/net.splitcells.os.state.interface/testing
repo.clone.into.current.test > /dev/null 2>&1 || echo.error '"'repo.clone.into.current'"' has no valid implementation.
command.managed.execute.test
user.ssh.key.generate.test
repo.process.test
this.requires test.dependencies.0 && command.managed.execute conjunction test.dependencies
rm -rf ~/.cache/net.splitcells.os.state.interface/testing