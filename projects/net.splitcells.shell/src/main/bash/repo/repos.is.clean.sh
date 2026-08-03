#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Exits with 0, if this repo can be synchronized and else exits 1.
# If uncommitted changes are present, the repo cannot be synchronized.

# This can be used as a safe guard for automated synchronization commands.
# An implementation has to exit with 0, if the implementation is not suited for the current repo type.
# For example, an implementation for Git has to exit with 0 for SVN repositories.

repos.process --command 'command.managed.execute disjunction repo.is.clean'
