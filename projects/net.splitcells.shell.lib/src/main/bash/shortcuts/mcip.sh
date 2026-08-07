#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# mcip aka. mvn clean install with parent
# Builds the current project's copy at a temporary folder,
# so that the user can continue working on the current project without interfering with the build.
# Thereby, the parent project is copied as well, in order to comply with Maven's relative path inside POMs.
set -x
set -e
currentFolder=$(basename "$PWD")
current=$(pwd)
mktemp=$(mktemp --directory)
trap "rm -r $mktemp" EXIT
echo Building project at $mktemp/
cp -a "$current/../." "$mktemp/"
cd $mktemp/$currentFolder
mvn clean install $@
