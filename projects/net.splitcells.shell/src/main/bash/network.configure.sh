#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Configures the current user's resources, that are reachable by network.
# For example, this command may setup or update another computer.
# Other use case is backing up or synchronizing personal data.

# It is recommended, that every instance of this command should connect to
# the resource via a local host name or via DNS.

command.managed.execute conjunction network.configure
