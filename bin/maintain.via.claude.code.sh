#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
set -x
 
git add . && git diff --quiet && git diff --cached --quiet || sh -c "echo There are changes left to commit. ; exit 1"
 
bin/claude.code.prompt.sh "Maintain the changelogs."
