#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Everything is tested step by step.
# Aborting this script in the middle should nevertheless save all meta data like the Network Logs.
# In other words, a partial execution should be considered a successful execution as well,
# as this test takes quite a while.
# Therefore, every command has to store its test data.

set -e
set -x # Makes it easier to debug problems on a remote server, especially because of its long runtime.
export JAVA_VERSION=21 # This is required on FreeBSD, if an older Java version is set as default.
current=$(pwd)
reposFolder="$(pwd)/../"
rm -rf ~/.m2/repository/net/splitcells/ # Our packages are deleted, as SNAPSHOT builds are often used, which can create a cache problems in the build.
# Build BOMs first, as otherwise `mvn wrapper:wrapper` will not run, because the dependency BOM with the scope import cannot be read.
  pwd # TODO Remove this debug statement.
  ls -al # TODO Remove this debug statement.
  cd "$reposFolder/net.splitcells.network.bom.base"
  pwd # TODO Remove this debug statement.
  ls -al # TODO Remove this debug statement.
  mvn clean install
  cd "$reposFolder/net.splitcells.network.bom"
  mvn clean install
cd "$reposFolder/net.splitcells.network.hub" # TODO Move this into worker.boostrap, when it the live server is supported by the new worker.process.
  mvn clean install
  mvn verify -Dtest.groups=testing_integration -DexcludedGroups="experimental_test"
  # TODO mvn clean install -Dtest.groups=testing_capabilities -DexcludedGroups="experimental_test"
  # TODO mvn clean install -Dtest.groups=benchmarking_runtime -DexcludedGroups="experimental_test"
  # TODO mvn clean install -Dtest_via_pitest_enabled=1 -Dtest_via_junit_disabled=1
  # TODO mvn clean install -Dreport_disabled=1
  # TODO mvn clean install -Dare-dependencies-up-to-date=true
cd "$reposFolder/net.splitcells.network"
  . bin/worker.bootstrap
# TODO Analyze daily CI problem. Move this to `. bin/worker.bootstrap`, after this is fixed.
  pwd
  ls -al
  cd "$reposFolder/net.splitcells.network"
  echo $PATH
  set +e
  ls $(echo $PATH | tr ':' ' ')
  set -e
# Continue normal work
  bin/build.part.with.python
cd "$reposFolder/net.splitcells.network/projects/net.splitcells.network.system"
  mvn exec:java -Dexec.mainClass=net.splitcells.network.worker.via.java.Tester "-Dexec.args=$(hostname)"
# TODO Analyze daily CI problem. Move this to `. bin/worker.bootstrap`, after this is fixed.
  cd "$reposFolder/net.splitcells.network"
  echo $PATH
  set +e
  ls $(echo $PATH | tr ':' ' ')
  set -e
  bin/repos.verify
