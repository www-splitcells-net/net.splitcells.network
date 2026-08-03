#!/usr/bin/env bash
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

user.bin.configure
command.managed.execute conjunction user.configure
system.managed.automatically.by.user.assert && system.update
 