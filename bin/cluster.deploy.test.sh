#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# The word cluster and not network is used, in order to have a dedicated name for deployed server.
# Thereby, the word network is not overloaded with different meanings.
# Deploys `repos.test` on all available servers.

../net.splitcells.network.hub/bin/net.splitcells.shell.repos.peers \
  | xargs -i sh -c "cd ../{} && test -f bin/cluster.node.ssh.addresses.sh && bin/cluster.node.ssh.addresses.sh" \
  | xargs -i sh -c "ssh -T {} echo 1 && bin/worker.test.at {} || echo Skipping {}"