#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Configures the current user's resources, that are reachable by network.
# For example, this command may setup or update another computer.
# Other use case is backing up or synchronizing personal data.

# It is recommended, that every instance of this command should connect to
# the resource via a local host name or via DNS.

command.managed.execute conjunction network.configure
