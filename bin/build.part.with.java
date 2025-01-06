#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
#
# This command exists, so that the project can be build with one command.
# This makes usage of tools building this project easier,
# as it can be hard to understand in such tools,
# how to chain commands calls.
set -e
export JAVA_VERSION=21 # This is required on FreeBSD, if an older Java version is set as default.
mvn clean install
