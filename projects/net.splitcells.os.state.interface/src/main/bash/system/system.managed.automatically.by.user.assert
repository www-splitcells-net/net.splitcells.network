#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

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
