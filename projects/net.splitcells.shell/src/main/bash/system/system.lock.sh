#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Locks all input and output devices of the system users' (i.e. lockscreen).

command.managed.execute disjunction system.lock
