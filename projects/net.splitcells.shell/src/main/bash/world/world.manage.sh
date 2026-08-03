#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Applies all configurations and updates to the world.
# The world contains and represents all top level concepts
# and therefore is a representation of all accessible objects.
#
# This command documents all configurations, that can be applied by OS state
# interface project.

user.configure
system.configure
system.update
network.configure