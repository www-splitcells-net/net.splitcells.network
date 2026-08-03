#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

command.managed.execute conjunction system.configure
# "system.cleanup" should not be executed during each "system.configure", as some commands require restarts in order to take place.
# See update via PackageKit for such an example.
