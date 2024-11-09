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

# Creates a mirror of a source repository.
# The difference between a repository clone and a mirror is, that a mirror is not intended to work independently.
# A mirror just copies an other repository.
# The source repositories URL is stored in the mirror and may be changed to different URL.
# The source repository may also be changed completely.
# The main purpose of a mirror is to save storage compared to clone.
# Note that it is not guaranteed that storage is used more efficient by an mirror than a clone.
# This depends on the implementation.

repos.process --command 'command.managed.execute disjunction repo.mirror '$@'/$subRepo'
