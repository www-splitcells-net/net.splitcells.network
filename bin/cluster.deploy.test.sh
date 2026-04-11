#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# The word cluster and not network is used, in order to have a dedicated name for deployed server.
# Thereby, the word network is not overloaded with different meanings.
# Deploys `repos.test` on all available servers.

mkdir -p target/cluster.deploy.test.sh
../net.splitcells.network.hub/bin/net.splitcells.shell.repos.peers \
  | xargs -i sh -c "cd ../{} && test -f bin/cluster.nodes.sh && bin/cluster.nodes.sh" \
  | xargs -i -P 10 sh -c "ssh -T {} echo 1 && bin/worker.test.at {} > target/cluster.deploy.test.sh/{}.log 2> target/cluster.deploy.test.sh/{}.error.log || echo Skipping {}"