#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

echo Setting up net.splitcells.shell for developers. The repos are located at ~/Documents/projects/net.splitcells.martins.avots.support.system/public

# Setup repos.
  repos=~/Documents/projects/net.splitcells.martins.avots.support.system/public
  mkdir -p $repos
  cd $repos
  test -d net.splitcells.network || git clone ssh://git@codeberg.org/splitcells-net/net.splitcells.network.git
  test -d net.splitcells.network.hub || git clone ssh://git@codeberg.org/splitcells-net/net.splitcells.network.hub.git
# Update repos.
  cd $repos/net.splitcells.network
  bin/repos.pull
# Setup Shell project.
  cd $repos/projects/net.splitcells.shell
  ./bin/install
  . src/main/bash/command/managed/command.managed.export.bin.sh
# Configure Shell project.
  cd $repos/../net.splitcells.network.hub
  bin/net.splitcells.shell.projects.peers.for.commands | xargs -i sh -c 'command.repository.register $(realpath {})'
  user.bin.configure
  command.managed.environment.configure