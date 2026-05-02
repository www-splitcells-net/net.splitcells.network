#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

echo Setting up net.splitcells.shell for developers. The repos are located at ~/Documents/projects/net.splitcells.martins.avots.support.system/public

coreRepo=~/Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network
cd $coreRepo/projects/net.splitcells.shell
./bin/install
. src/main/bash/command/managed/command.managed.export.bin.sh
cd $coreRepo/../net.splitcells.network.hub
bin/net.splitcells.shell.projects.peers.for.commands | xargs -i sh -c 'command.repository.register $(realpath {})'
user.bin.configure
command.managed.environment.configure