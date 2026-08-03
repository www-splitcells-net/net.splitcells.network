#!/usr/bin/env bash
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
