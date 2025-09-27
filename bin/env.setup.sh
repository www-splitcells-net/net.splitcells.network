#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Execute this command via `. bin/env.setup.sh`, in order to setup the environment of the current shell.

mvn wrapper:wrapper -Dmaven=3.9.9 # Sets up the Maven version, in order to avoid build problems regarding the site goal.
export PATH="$(realpath ./):$PATH"