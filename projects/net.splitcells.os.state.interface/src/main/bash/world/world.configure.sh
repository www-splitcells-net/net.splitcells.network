#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

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