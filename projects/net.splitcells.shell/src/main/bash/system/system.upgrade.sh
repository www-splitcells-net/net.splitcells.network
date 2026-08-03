#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

command.managed.execute conjunction system.upgrade.prepare
command.managed.execute conjunction system.upgrade.start
# A "system.upgrade.start" may or may not restart the system.
# This is part of the operation systems policy.
