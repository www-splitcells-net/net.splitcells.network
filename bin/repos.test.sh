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
  # TODO mvn exec:java -Dexec.mainClass=net.splitcells.network.worker.via.java.Tester "-Dexec.args=$(hostname)"
cd "$reposFolder/net.splitcells.network" # Creating reports is done last, so the caller can use the reports.
  # Integration tests need to be enabled here as well, as verify does not only start the source code check, but also reruns the test and therefore the coverage.
  # The JaCoCo report is created only for the main report, as otherwise the Distro repo causes errors.
  # `-Dsonar.inclusions=src/main/**,bin/*` does not seem to work.`
  # Enabling debugg logging via -X makes it easier to find reason for errors in daily CI.
  # SonarCloud:
  # * Adding the following to the mvn verify command did not work and broke authorization.
  #   The install step is required, according to SonarCloud doc https://docs.sonarsource.com/sonarqube-server/10.8/analyzing-source-code/scanners/sonarscanner-for-maven#analyzing
  # * If one gets an 403 HTTP status code, consider regenerating the SONAR_TOKEN: https://sonarcloud.io/account/security
  mvn -B clean install \
    org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    -Dsonar.projectKey=www-splitcells-net_net.splitcells.network \
    -Dsonar.organization=www-splitcells-net \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.test.exclusions=src/main/java/**
  # Clean has to be executed, as otherwise the code coverage report will be missing for `repos.upload.reports.sh`.
  mvn clean install site \
      -Dtest_codecov=1 \
      -Dsource_code_check=1 \
      -Dtest.groups=testing_unit,testing_integration \
      -DexcludedGroups="experimental_test"
  mvn -B clean install \
    org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    -Dsonar.projectKey=www-splitcells-net_net.splitcells.network \
    -Dsonar.organization=www-splitcells-net \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.test.exclusions=src/main/java/**
    -Dtest_codecov=1 \
    -Dsource_code_check=1 \
    -Dtest.groups=testing_unit,testing_integration \
    -DexcludedGroups="experimental_test"
# TODO Analyze daily CI problem. Move this to `. bin/worker.bootstrap`, after this is fixed.
  cd "$reposFolder/net.splitcells.network"
  echo $PATH
  set +e
  ls $(echo $PATH | tr ':' ' ')
  set -e
  bin/repos.verify
