#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# This command states whether certain things that should be possible, cannot be done, because the system has a certain state.
# Examples:
# * Check if system.update can be executed by checking, if internet connectivity is available or if some updates are locally available (i.e. downloaded previously).
# * Measure network package round trip time for pings etc., in order to test.routine if web browsing is impaired.
# * Warning that system.shutdown should not be done because updates are being installed.
command.managed.execute conjunction system.state
