#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

log.error THis command is deprecated, because it uses positional arguments.
repos.process --command-for-current 'command.managed.execute disjunction repo.push.to '$@'/$subRepo'
