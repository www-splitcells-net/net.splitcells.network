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

# Exits with 0, if the system should be managed automatically.

# If the system is not managed automatically commands of this framework (i.e. "system.configure")
# will do only things, that the user explicitly enabled.
# Especially, it is ensured that the system is not changed without the explicit order
# or configuration of the user.

# If the system is managed automatically commands of this framework may may
# apply some changes to the system automatically.
# In other words, commands of the frameworks will apply some default configurations.
# For instance, the execution of "user.configure" will also update the system.

command.managed.execute conjunction system.managed.automatically.by.user.assert
