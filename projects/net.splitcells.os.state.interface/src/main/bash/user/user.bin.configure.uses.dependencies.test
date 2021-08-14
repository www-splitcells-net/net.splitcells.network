#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Exits with 0, if every call of "user.bin.configure" should execute "dependencies.test" at the end.
# Otherwise this command exits with 1.

. this.requires user.bin.configure.uses.dependencies.test.0
command.managed.execute disjunction user.bin.configure.uses.dependencies.test