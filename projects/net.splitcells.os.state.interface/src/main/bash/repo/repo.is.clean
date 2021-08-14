#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Exits with 0, if this repo can be synchronized and else exits 1.
# If uncommitted changes are present, the repo cannot be synchronized.

# This can be used as a safe guard for automated synchronization commands.
# An implementation has to exit with 0, if the implementation is not suited for the current repo type.
# For example, an implementation for Git has to exit with 0 for SVN repositories.

repo.process --command 'command.managed.execute disjunction repo.is.clean'
