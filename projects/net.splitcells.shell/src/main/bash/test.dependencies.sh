#!/usr/bin/env bash
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