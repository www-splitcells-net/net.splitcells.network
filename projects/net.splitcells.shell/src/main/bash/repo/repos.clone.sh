#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Clones a repository into the current folder.
# TODO FIX Recursive cloning does not work.

repos.process --command 'command.managed.execute disjunction repo.clone '$@'/$subRepo'
