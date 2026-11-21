#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
 
git add . && git diff --quiet && git diff --cached --quiet || sh -c "echo There are changes left to commit. ; exit 1"
 
bin/ai.prompt.sh "Maintain the changelogs."
bin/ai.prompt.sh "Pick a random documentation file and check it."
bin/ai.prompt.sh "Pick a random source code file and check it's comments."
bin/ai.prompt.sh "Search for security vulnerabilities in source code."
bin/ai.prompt.sh "Pick a random part of the project. How do you like or not like the architecture of that part?"