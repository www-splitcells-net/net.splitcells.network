#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Any implementation of `repo.remote.set`, should be an offline command.
# Therefore, the command should not create an error, if the remote repo does not exist.
# For one, there might be temporal connectivity issues,
# that should not cause problems by default, when repos are configured locally.
# Secondly, if the remote repo hoster deletes a repo and updates the `repos.process` data accordingly,
# an online version of `repo.remote.set` would cause problems with a very limited number of benefits. 

repos.process --command "command.managed.execute disjunction repo.remote.set $@/\${childRepo}"
