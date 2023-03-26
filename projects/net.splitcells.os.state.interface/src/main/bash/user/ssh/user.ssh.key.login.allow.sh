#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
# Note that mounting or symlinking "~/.ssh" may not working,
# because right management is not always possible for such on all platforms.
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
 