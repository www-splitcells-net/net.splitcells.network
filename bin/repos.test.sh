#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Everything is tested step by step.
# Aborting this script in the middle should nevertheless save all meta data like the Network Logs.
# In other words, a partial execution should be considered a successful execution as well,
# as this test takes quite a while.
# Therefore, every command has to store its test data.

set -e
export JAVA_VERSION=21 # This is required on FreeBSD, if an older Java version is set as default.
cd ../net.splitcells.network.hub # TODO Move this into worker.boostrap, when it the live server is supported by the new worker.process.
  rm -rf ~/.m2/repository/net/splitcells/ # Our packages are deleted, as SNAPSHOT builds are often used, which can create a cache problems in the build.
  mvn clean install
  mvn clean install -Dtest.groups=testing_integration -DexcludedGroups="experimental_test"
  mvn clean install -Dtest.groups=testing_capabilities -DexcludedGroups="experimental_test"
  mvn clean install -Dtest.groups=benchmarking_runtime -DexcludedGroups="experimental_test"
  mvn clean install -Dtest_via_pitest_enabled=1 -Dtest_via_junit_disabled=1
  mvn clean install -Dreport_disabled=1
  mvn clean install -Dare-dependencies-up-to-date=true
cd ../net.splitcells.network
  bin/build.part.with.python
cd ../net.splitcells.network/projects/net.splitcells.network.system
  mvn exec:java -Dexec.mainClass=net.splitcells.network.worker.via.java.Tester "-Dexec.args=$(hostname)"
