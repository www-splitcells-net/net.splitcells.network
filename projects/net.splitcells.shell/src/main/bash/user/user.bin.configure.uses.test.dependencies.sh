#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Exits with 0, if every call of "user.bin.configure" should execute "test.dependencies" at the end.
# Otherwise this command exits with 1.

. this.requires user.bin.configure.uses.test.dependencies.0
command.managed.execute disjunction user.bin.configure.uses.test.dependencies