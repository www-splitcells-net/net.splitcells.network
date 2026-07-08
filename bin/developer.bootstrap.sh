#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# TODO Should this be only a call to bin/worker.bootstrap with additional config and tooling setup, in order to avoid duplicate code?

set -e
set -x

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
  cd $repos/net.splitcells.network/projects/net.splitcells.shell
  ./bin/install
  . src/main/bash/command/managed/command.managed.export.bin.sh
# Configure Shell project.
  cd $repos/net.splitcells.network.hub
  project.repository.register $(realpath .)
  project.repository.register $(realpath ../net.splitcells.network)
  bin/net.splitcells.shell.projects.peers.for.commands | xargs -i sh -c 'command.repository.register $(realpath {})'
  bin/net.splitcells.shell.projects.peers.for.commands | xargs -i sh -c 'project.repository.register $(realpath {})'
  bin/net.splitcells.shell.repos.peers | xargs -i sh -c 'project.repository.register $(realpath ../{})'
  cd $repos/net.splitcells.network/projects
  find . -maxdepth 1 -type d | xargs -i sh -c 'project.repository.register $(realpath ./{})'
  # TODO Remove echo code.
  echo command.repositories
  cat ~/.config/net.splitcells.shell/command.repositories
  cat ~/.config/net.splitcells.shell/project.repositories
  user.bin.configure
  command.managed.environment.configure
# Setup programs like Maven at the correct version and enable SSH connection cache.
  cd $repos/net.splitcells.network/
  env.setup.sh
