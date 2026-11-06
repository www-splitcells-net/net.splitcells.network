#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

repos.synchronize.with --remote-host=codeberg.org --remote-repo=git@codeberg.org:splitcells-net/\${childRepo}.git --remote-name=Codeberg3
repos.synchronize.with --remote-host=codeberg.org --remote-repo=git@github.com:www-splitcells-net/\${childRepo}.git --remote-name=GitHub3