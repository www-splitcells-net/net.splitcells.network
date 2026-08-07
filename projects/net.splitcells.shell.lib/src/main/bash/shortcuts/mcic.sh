#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# mcic aka. mvn clean install current
# Builds the current project's copy at a temporary folder,
# so that the user can continue working on the current project without interfering with the build.
set -x
set -e
current=$(pwd)
mktemp=$(mktemp --directory)
trap "rm -r $mktemp" EXIT
echo Building project at $mktemp/
cp -a "$current/." "$mktemp/"
cd $mktemp
mvn clean install $@
