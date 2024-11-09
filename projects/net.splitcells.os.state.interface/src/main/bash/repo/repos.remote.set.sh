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

# Any implementation of `repo.remote.set`, should be an offline command.
# Therefore, the command should not create an error, if the remote repo does not exist.
# For one, there might be temporal connectivity issues,
# that should not cause problems by default, when repos are configured locally.
# Secondly, if the remote repo hoster deletes a repo and updates the `repos.process` data accordingly,
# an online version of `repo.remote.set` would cause problems with a very limited number of benefits. 

repos.process --command "command.managed.execute disjunction repo.remote.set $@/\$subRepo"
