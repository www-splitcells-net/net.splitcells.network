#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Execute this command via `. bin/env.setup.sh`, in order to setup the environment of the current shell.

set -e
set -x
# Build BOMs first, as otherwise `mvn wrapper:wrapper` will not run, because the dependency BOM with the scope import cannot be read.
  cd ../net.splitcells.network.bom.base
  mvn clean install
  cd ../net.splitcells.network.bom
  mvn clean install
cd ../net.splitcells.network
  mvn wrapper:wrapper -Dmaven=3.9.9 # Sets up the Maven version, in order to avoid build problems regarding the site goal.
  export PATH="$(realpath ./):$PATH"
  echo "$(pwd)/mvnw \$@" > mvn
  chmod +x mvn
  # Print crash logs, in order to debug CI workflow crashes.
  export MAVEN_OPTS="-XX:ErrorFile=$HOME/hs_err_pid%p.log"
  trap "cat $HOME/hs_err_pid*.log || echo No JVM crash reports are present." EXIT
. ./projects/net.splitcells.shell/src/main/bash/command/managed/command.managed.export.bin.sh