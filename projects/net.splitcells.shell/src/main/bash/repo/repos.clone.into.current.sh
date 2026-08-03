#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Clones a repository into the current folder.

repos.process --command 'command.managed.execute disjunction repo.clone.into.current '$@'/$subRepo' --command-for-missing 'command.managed.execute disjunction repo.clone.into.current '$@'/$subRepo'
