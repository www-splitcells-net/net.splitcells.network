#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# This command states whether certain things that should be possible, cannot be done, because the system has a certain state.
# Examples:
# * Check if system.update can be executed by checking, if internet connectivity is available or if some updates are locally available (i.e. downloaded previously).
# * Measure network package round trip time for pings etc., in order to test.routine if web browsing is impaired.
# * Warning that system.shutdown should not be done because updates are being installed.
command.managed.execute conjunction system.state
