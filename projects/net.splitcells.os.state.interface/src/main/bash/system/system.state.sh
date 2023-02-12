#!/usr/bin/env bash
# Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the Eclipse
# Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
# which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This command states whether certain things that should be possible, cannot be done, because the system has a certain state.
# Examples:
# * Check if system.update can be executed by checking, if internet connectivity is available or if some updates are locally available (i.e. downloaded previously).
# * Measure network package round trip time for pings etc., in order to test.routine if web browsing is impaired.
# * Warning that system.shutdown should not be done because updates are being installed.
command.managed.execute conjunction system.state
