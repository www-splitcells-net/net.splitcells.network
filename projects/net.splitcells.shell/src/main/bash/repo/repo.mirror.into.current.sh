#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Clones a repository into the current folder.

if ssh $1 "test -d ..net.splitcells.shell.repo"; then
	repos.clone.into.current $@
	fi
repos.process --command 'command.managed.execute disjunction repo.mirror.into.current '$@'/$subRepo'
