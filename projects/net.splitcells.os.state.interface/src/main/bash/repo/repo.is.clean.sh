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

# Exits with 0, if this repo can be synchronized and else exits 1.
# If uncommitted changes are present, the repo cannot be synchronized.

# This can be used as a safe guard for automated synchronization commands.
# An implementation has to exit with 0, if the implementation is not suited for the current repo type.
# For example, an implementation for Git has to exit with 0 for SVN repositories.

repo.process --command 'command.managed.execute disjunction repo.is.clean'
