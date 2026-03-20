#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Execute tests, that may not be stable, as the rely on hard to control details of the test runners themselves. 

set -e
set -x # Makes it easier to debug problems on a remote server, especially because of its long runtime.
export JAVA_VERSION=21 # This is required on FreeBSD, if an older Java version is set as default.
mvn verify \
    -Dtest.groups=benchmarking_runtime \
    -DexcludedGroups=experimental_test,testing_unit,testing_integration,testing_capabilities