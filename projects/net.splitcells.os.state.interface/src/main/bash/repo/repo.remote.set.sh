#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Any implementation of `repo.remote.set`, should be an offline command.
# Therefore, the command should not create an error, if the remote repo does not exist.
# For one, there might be temporal connectivity issues,
# that should not cause problems by default, when repos are configured locally.
# Secondly, if the remote repo hoster deletes a repo and updates the `repo.process` data accordingly,
# an online version of `repo.remote.set` would cause problems with a very limited number of benefits. 

repo.process --command "command.managed.execute disjunction repo.remote.set $@/\$subRepo"
