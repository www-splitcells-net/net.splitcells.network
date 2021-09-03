#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Its main goal is to provide a good experience for the computer user.
# These configuration should be as neutral to the user's preferences as possible
# and should never get in the way of the user.

# Makes configurations, that should be done on all normal desktop computers.
# Only configurations that fit to the computers should be done,
# like installing graphics driver, if not present.

command.managed.execute conjunction system.configure.auto
